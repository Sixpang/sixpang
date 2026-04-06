package org.sixpang.hubservice.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sixpang.commonserver.global.CustomException;
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

        if (allRoutes.isEmpty()) {
            throw new CustomException(RouteErrorCode.OPTIMAL_ROUTE_NOT_FOUND);
        }

        Map<UUID, List<Route>> graph = buildGraphUnderLimit(allRoutes);

        Map<UUID, BigDecimal> shortestDistance = new HashMap<>();
        Map<UUID, Route> previousEdge = new HashMap<>(); // 지나온 Route 정보 기록

        PriorityQueue<NodeDistance> pq =
                new PriorityQueue<>(Comparator.comparing(NodeDistance::getDistance));

        // 초기화 시 모든 관련 노드 추가
        allRoutes.forEach(r -> {
            shortestDistance.put(r.getDepartureHubId(), new BigDecimal("999999"));
            shortestDistance.put(r.getArrivalHubId(), new BigDecimal("999999"));
        });

        if (!shortestDistance.containsKey(departureHubId)) {
            throw new CustomException(RouteErrorCode.OPTIMAL_ROUTE_NOT_FOUND);
        }

        shortestDistance.put(departureHubId, BigDecimal.ZERO);
        pq.add(new NodeDistance(departureHubId, BigDecimal.ZERO));

        // 다익스트라 탐색
        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();

            if (current.getHubId().equals(arrivalHubId)) break;

            for (Route edge : graph.getOrDefault(current.getHubId(), List.of())) {

                BigDecimal newDist = current.getDistance().add(edge.getDistance());

                if (newDist.compareTo(shortestDistance.getOrDefault(edge.getArrivalHubId(), new BigDecimal("999999"))) < 0) {
                    shortestDistance.put(edge.getArrivalHubId(), newDist);
                    previousEdge.put(edge.getArrivalHubId(), edge);
                    pq.add(new NodeDistance(edge.getArrivalHubId(), newDist));
                }
            }
        }

        if (!previousEdge.containsKey(arrivalHubId)) {
            throw new CustomException(RouteErrorCode.OPTIMAL_ROUTE_NOT_FOUND);
        }

        // 경로 재구성
        List<Route> path = new ArrayList<>();
        UUID step = arrivalHubId;

        while (previousEdge.containsKey(step)) {
            Route edge = previousEdge.get(step);
            path.add(edge);
            step = edge.getDepartureHubId();
        }

        Collections.reverse(path);

        // 결과 DTO 조립
        BigDecimal totalDistance = BigDecimal.ZERO;
        long totalDuration = 0L;

        List<PathResponse> pathList = new ArrayList<>();

        int seq = 1;
        for (Route r : path) {
            totalDistance = totalDistance.add(r.getDistance());
            totalDuration += r.getDuration();
            pathList.add(PathResponse.from(seq++, r));
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

        if (existingHubs.isEmpty()) {
            log.info("생성할 기존 허브가 없어 Route 생성 생략: newHubId={}", newHubId);
            return;
        }

        // 네이버 맵 API를 호출하여 경로 생성 및 저장
        for (Hub targetHub : existingHubs) {
            createRouteOrThrow(newHub, targetHub); // 정방향
            createRouteOrThrow(targetHub, newHub); // 역방향
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
    private void createRouteOrThrow(Hub start, Hub goal) {

        if (routeRepository.existsByDepartureHubIdAndArrivalHubId(start.getId(), goal.getId())) {
            return;
        }

        DirectionsResponseDto response;

        try {
            response = naverMapFeignClient.getRoute(
                    naverClientId,
                    naverClientSecret,
                    start.getLongitude() + "," + start.getLatitude(),
                    goal.getLongitude() + "," + goal.getLatitude()
            );
        } catch (Exception e) {
            log.error("네이버 API 호출 실패", e);
            throw new CustomException(RouteErrorCode.ROUTE_GENERATION_FAILED);
        }

        validateResponse(response);

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
    }

    private void validateResponse(DirectionsResponseDto response) {

        if (response == null ||
                response.route() == null ||
                response.route().traoptimal() == null ||
                response.route().traoptimal().isEmpty() ||
                response.route().traoptimal().get(0).summary() == null) {

            throw new CustomException(RouteErrorCode.NAVER_API_RESPONSE_INVALID);
        }
    }
}
