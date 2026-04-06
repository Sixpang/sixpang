package org.sixpang.deliveryservice.infrastructure.strategy;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.HubRouteInfo;
import org.sixpang.deliveryservice.application.dto.RouteCalculationResult;
import org.sixpang.deliveryservice.application.service.strategy.RouteCalculationStrategy;
import org.sixpang.deliveryservice.infrastructure.client.HubRouteClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExternalApiRouteCalculationStrategy implements RouteCalculationStrategy {
    private final HubRouteClient hubRouteClient;

    public RouteCalculationResult calculate(UUID departureHub, UUID arrivalHub) {
        HubRouteInfo routeInfo = hubRouteClient.findOptimalRoute(departureHub, arrivalHub);
        return new RouteCalculationResult(routeInfo.estimatedDistance(), routeInfo.estimatedTime());
    }
}
