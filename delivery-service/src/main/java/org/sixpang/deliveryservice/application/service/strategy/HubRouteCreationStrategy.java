package org.sixpang.deliveryservice.application.service.strategy;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.RouteCalculationResult;
import org.sixpang.deliveryservice.application.service.service.DeliveryManagerService;
import org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubRouteCreationStrategy implements RouteCreationStrategy {
    private final RouteCalculationStrategy routeCalculationStrategy;
    private final DeliveryManagerService deliveryManagerService;

    @Override
    public List<DeliveryRoute> createRoutes(UUID departureHubId, UUID arrivalHubId) {
        // 허브 간 경로 계산
        RouteCalculationResult result = routeCalculationStrategy.calculate(departureHubId, arrivalHubId);

        // 경로가 없으면 빈 리스트 반환
        if (result.hubPath().isEmpty()) {
            return List.of();
        }

        List<DeliveryRoute> routes = new ArrayList<>();

        // 각 경로마다 DeliveryRoute 생성
        for (int i = 0; i < result.hubPath().size() - 1; i++) {
            UUID currentHub = result.hubPath().get(i);
            UUID nextHub = result.hubPath().get(i + 1);

            // 해당 구간의 허브 배송 담당자 배정
            HubDeliveryManager manager = deliveryManagerService.assignHubManager();

            DeliveryRoute route = DeliveryRoute.builder()
                    .hubSequence(i + 1)
                    .departureHub(currentHub)
                    .arrivalHub(nextHub)
                    .hubRouteId(UUID.randomUUID()) // 실제로는 HubRoute 서비스에서 가져와야 함
                    .estimatedDistance(result.estimatedDistance())
                    .estimatedTime(result.estimatedTime())
                    .hubDeliveryManagerId(manager.getId())
                    .build();

            routes.add(route);
        }

        return routes;
    }
}
