package org.sixpang.hubservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.GlobalErrorCode;
import org.sixpang.hubservice.application.dto.*;
import org.sixpang.hubservice.domain.model.entity.Hub;
import org.sixpang.hubservice.domain.model.entity.Route;
import org.sixpang.hubservice.domain.model.enums.HubStatus;
import org.sixpang.hubservice.domain.repository.HubRepository;
import org.sixpang.hubservice.domain.repository.RouteRepository;
import org.sixpang.hubservice.exception.HubErrorCode;
import org.sixpang.hubservice.exception.RouteErrorCode;
import org.sixpang.hubservice.infrastructure.NaverMapFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final HubRepository hubRepository;
    private final NaverMapFeignClient naverMapFeignClient;
    private static final BigDecimal DISTANCE_LIMIT = new BigDecimal("200.0"); // 거리 제한: 200km

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    // 특정 허브에서 이동 가능한 경로 조회
    @Transactional(readOnly = true)
    @Cacheable(value = "routes", key = "'available:' + #departureHubId")
    public List<AvailableRouteResponseDto> getAvailableRoutes(UUID departureHubId) {
        List<Route> routes = routeRepository.findAllByDepartureHubIdAndDeletedAtIsNull(departureHubId);

        if (routes.isEmpty()) {
            throw new CustomException(RouteErrorCode.AVAILABLE_ROUTE_NOT_FOUND);
        }

        return routes.stream()
                .map(AvailableRouteResponseDto::from)
                .collect(Collectors.toList());
    }

    // 특정 두 허브 간 직통 경로 조회 (거리, 시간)
    @Transactional(readOnly = true)
    @Cacheable(value = "routes", key = "'direct:' + #startHubId + ':' + #goalHubId")
    public DirectRouteResponseDto getDirectRoutes(UUID startHubId, UUID goalHubId) {
        Route route = routeRepository.findByDepartureHubIdAndArrivalHubIdAndDeletedAtIsNull(startHubId, goalHubId)
                .orElseThrow(() -> new CustomException(RouteErrorCode.DIRECT_ROUTE_NOT_FOUND));

        return DirectRouteResponseDto.from(route);
    }

    // 최적 이동 경로 조회 (다익스트라 : 200km 제약 조건)
    @Transactional(readOnly = true)
    @Cacheable(value = "routes", key = "'optimal:' + #startHubId + ':' + #goalHubId")
    public OptimalRouteResponseDto findOptimalRoute(UUID departureHubId, UUID arrivalHubId) {

        // 활성화된 모든 경로 조회 및 200km 미만 그래프 생성
        List<Route> allRoutes = routeRepository.findAllByDeletedAtIsNull();
        Map<UUID, List<Route>> graph = buildGraphUnderLimit(allRoutes);

        Map<UUID, BigDecimal> shortestDistances = new HashMap<>();
        Map<UUID, Route> edgeTo = new HashMap<>(); // 지나온 Route 정보 기록
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparing(NodeDistance::getDistance));

        // 초기화 시 모든 관련 노드 추가
        allRoutes.forEach(r -> {
            shortestDistances.put(r.getDepartureHubId(), new BigDecimal("999999.0"));
            shortestDistances.put(r.getArrivalHubId(), new BigDecimal("999999.0"));
        });

        if (!shortestDistances.containsKey(departureHubId)) {
            throw new CustomException(RouteErrorCode.OPTIMAL_ROUTE_NOT_FOUND);
        }

        shortestDistances.put(departureHubId, BigDecimal.ZERO);
        pq.add(new NodeDistance(departureHubId, BigDecimal.ZERO));

        // 다익스트라 탐색
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            UUID currentHubId = current.getHubId();
            BigDecimal currentDist = current.getDistance();

            if (currentHubId.equals(arrivalHubId)) break;
            if (currentDist.compareTo(shortestDistances.get(currentHubId)) > 0) continue;

            List<Route> edges = graph.getOrDefault(currentHubId, new ArrayList<>());
            for (Route edge : edges) {
                UUID neighborHubId = edge.getArrivalHubId();
                BigDecimal newDist = currentDist.add(edge.getDistance());

                if (newDist.compareTo(shortestDistances.getOrDefault(neighborHubId, new BigDecimal("999999.0"))) < 0) {
                    shortestDistances.put(neighborHubId, newDist);
                    edgeTo.put(neighborHubId, edge);
                    pq.add(new NodeDistance(neighborHubId, newDist));
                }
            }
        }

        if (!edgeTo.containsKey(arrivalHubId)) {
            throw new CustomException(GlobalErrorCode.INVALID_REQUEST);
        }

        // 경로 재구성
        List<Route> pathEdges = new ArrayList<>();
        UUID step = arrivalHubId;
        while (edgeTo.containsKey(step)) {
            Route edge = edgeTo.get(step);
            pathEdges.add(edge);
            step = edge.getDepartureHubId();
        }
        Collections.reverse(pathEdges);

        // 결과 DTO 조립
        BigDecimal totalDistance = BigDecimal.ZERO;
        Long totalDuration = 0L;
        List<PathResponse> pathList = new ArrayList<>();

        int sequence = 1;
        for (Route edge : pathEdges) {
            totalDistance = totalDistance.add(edge.getDistance());
            totalDuration += edge.getDuration();

            // from 메서드에 sequence와 Route 엔티티만 넘김
            pathList.add(PathResponse.from(sequence++, edge));
        }

        return OptimalRouteResponseDto.from(totalDistance, totalDuration, pathList);
    }

    // 새 허브 등록/활성화 시 양방향 경로 자동 생성
    @Transactional
    @CacheEvict(value = "routes", allEntries = true)
    public void generateRoutesForNewHub(UUID newHubId) {

        Hub newHub = hubRepository.findByIdAndDeletedAtIsNull(newHubId)
                .orElseThrow(() -> new CustomException(HubErrorCode.HUB_NOT_FOUND));

        // 본인을 제외한 활성화된 모든 기존 허브 조회
        List<Hub> existingHubs = hubRepository.findAllByDeletedAtIsNull().stream()
                .filter(h -> h.getStatus() == HubStatus.ACTIVE)
                .filter(h -> !h.getId().equals(newHub.getId()))
                .toList();

        // 네이버 맵 API를 호출하여 경로 생성 및 저장
        for (Hub targetHub : existingHubs) {
            fetchAndSaveRoute(newHub, targetHub); // 정방향
            fetchAndSaveRoute(targetHub, newHub); // 역방향
        }
    }

    // 특정 허브 비활성화/삭제 시 관련 경로 삭제
    @Transactional
    @CacheEvict(value = "routes", allEntries = true)
    public void disableRoutesForHub(UUID hubId, UUID userId) {
        routeRepository.softDeleteRoutesByHubId(hubId, userId);
    }

    // 그래프 생성 (200km 이상 간선 필터링)
    private Map<UUID, List<Route>> buildGraphUnderLimit(List<Route> routes) {
        Map<UUID, List<Route>> graph = new HashMap<>();
        for (Route route : routes) {
            if (route.getDistance().compareTo(DISTANCE_LIMIT) >= 0) {
                continue;
            }
            graph.putIfAbsent(route.getDepartureHubId(), new ArrayList<>());
            graph.get(route.getDepartureHubId()).add(route);
        }
        return graph;
    }

    private static class NodeDistance {
        private final UUID hubId;
        private final BigDecimal distance;

        public NodeDistance(UUID hubId, BigDecimal distance) {
            this.hubId = hubId;
            this.distance = distance;
        }

        public UUID getHubId() { return hubId; }
        public BigDecimal getDistance() { return distance; }
    }

    // 네이버 맵 API 호출 및 Route 엔티티 저장
    private void fetchAndSaveRoute(Hub start, Hub goal) {
        try {
            var response = naverMapFeignClient.getRoute(
                    naverClientId,
                    naverClientSecret,
                    start.getLongitude() + "," + start.getLatitude(),
                    goal.getLongitude() + "," + goal.getLatitude()
            );

            var summary = response.route().traoptimal().get(0).summary();

            BigDecimal distanceKm = BigDecimal.valueOf(summary.distance())
                    .divide(new BigDecimal("1000"), 2, BigDecimal.ROUND_HALF_UP);

            Route route = Route.of(
                    start.getId(), start.getName(),
                    goal.getId(), goal.getName(),
                    (long) summary.duration(),
                    distanceKm
            );

            routeRepository.save(route);

        } catch (Exception e) {
            log.error("경로 생성 실패 (네이버맵 API 오류): {} -> {}", start.getName(), goal.getName(), e);
        }
    }
}
