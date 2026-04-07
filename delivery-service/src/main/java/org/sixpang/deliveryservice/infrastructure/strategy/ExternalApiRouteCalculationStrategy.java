package org.sixpang.deliveryservice.infrastructure.strategy;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.HubRouteInfo;
import org.sixpang.deliveryservice.application.dto.RouteCalculationResult;
import org.sixpang.deliveryservice.application.service.strategy.RouteCalculationStrategy;
import org.sixpang.deliveryservice.exception.DeliveryRouteErrorCode;
import org.sixpang.deliveryservice.infrastructure.client.HubRouteClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExternalApiRouteCalculationStrategy implements RouteCalculationStrategy {
    private final HubRouteClient hubRouteClient;

    public RouteCalculationResult calculate(UUID departureHub, UUID arrivalHub) {
        HubRouteInfo routeInfo = hubRouteClient.findOptimalRoute(departureHub, arrivalHub);
        if (routeInfo == null) {
            throw new CustomException(DeliveryRouteErrorCode.ROUTE_NOT_FOUND);
        }
        return new RouteCalculationResult(
                routeInfo.hubPath(),
                routeInfo.estimatedDistance(),
                routeInfo.estimatedTime()
        );
    }
}
