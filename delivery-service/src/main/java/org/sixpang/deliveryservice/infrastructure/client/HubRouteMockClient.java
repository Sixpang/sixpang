package org.sixpang.deliveryservice.infrastructure.client;

import org.sixpang.deliveryservice.application.dto.HubRouteInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class HubRouteMockClient implements HubRouteClient {

    @Override
    public HubRouteInfo findOptimalRoute(UUID departureHubId, UUID arrivalHubId) {
        // 1. 경로 데이터 (출발지와 도착지를 리스트로 묶음)
        List<UUID> path = List.of(departureHubId, arrivalHubId);

        // 2. 원하는 값으로 수정해서 반환
        return new HubRouteInfo(
                path,                           // 경로 (필수 추가)
                new BigDecimal("25.7"),         // 거리: 10.5에서 25.7로 수정 예시
                120L                            // 시간: 60에서 120으로 수정 예시
        );
    }
}
