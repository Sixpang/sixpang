package org.sixpang.deliveryservice.application.dto;

import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute;

import java.util.List;
import java.util.UUID;

public record DeliveryWithRoutesResponse(
        UUID id,
        UUID orderId,
        String status,
        UUID departureHub,
        UUID arrivalHub,
        String address,
        List<RouteInfo> routes
) {
    public static DeliveryWithRoutesResponse from(Delivery delivery) {
        List<RouteInfo> routeInfos = delivery.getDeliveryRoutes().stream()
                .map(RouteInfo::from)
                .toList();

        return new DeliveryWithRoutesResponse(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getStatus().name(),
                delivery.getDepartureHub(),
                delivery.getArrivalHub(),
                delivery.getAddress(),
                routeInfos
        );
    }

    public record RouteInfo(
            UUID id,
            Integer sequence,
            UUID departureHub,
            UUID arrivalHub,
            String status,
            Double estimatedDistance,
            Long estimatedTime
    ) {
        public static RouteInfo from(DeliveryRoute route) {
            return new RouteInfo(
                    route.getId(),
                    route.getHubSequence(),
                    route.getDepartureHub(),
                    route.getArrivalHub(),
                    route.getStatus().name(),
                    route.getEstimatedDistance().doubleValue(),
                    route.getEstimatedTime()
            );
        }
    }
}