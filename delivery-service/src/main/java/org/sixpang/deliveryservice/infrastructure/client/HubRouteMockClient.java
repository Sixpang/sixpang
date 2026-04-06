package org.sixpang.deliveryservice.infrastructure.client;

import org.sixpang.deliveryservice.application.dto.HubRouteInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class HubRouteMockClient implements HubRouteClient {

    @Override
    public HubRouteInfo findOptimalRoute(UUID departureHubId, UUID arrivalHubId) {
        // 고정된 가짜 데이터 반환 (거리 10.5km, 시간 60분)
        return new HubRouteInfo(
                new BigDecimal("10.5"),
                60L
        );
    }
}
