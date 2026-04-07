package org.sixpang.deliveryservice.application.dto;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.exception.DeliveryRouteErrorCode;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RouteCalculationResult(List<UUID> hubPath,
                                     BigDecimal estimatedDistance,
                                     Long estimatedTime
) {
    public RouteCalculationResult {
        if (hubPath == null || hubPath.isEmpty()) {
            throw new CustomException(DeliveryRouteErrorCode.DELIVERY_PATH_ERROR);
        }


        if (estimatedDistance == null || estimatedDistance.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(DeliveryRouteErrorCode.INVALID_ROUTE_DISTANCE);
        }

        if (estimatedTime == null || estimatedTime < 0) {
            throw new CustomException(DeliveryRouteErrorCode.INVALID_ROUTE_TIME);
        }
    }
}