package org.sixpang.deliveryservice.application.dto;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.exception.DeliveryRouteErrorCode;

import java.math.BigDecimal;

public record RouteCalculationResult(
        BigDecimal estimatedDistance,
        Long estimatedTime
) {
    public RouteCalculationResult {
        if (estimatedDistance == null || estimatedDistance.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(DeliveryRouteErrorCode.INVALID_ROUTE_DISTANCE);
        }
        if (estimatedTime == null || estimatedTime < 0) {
            throw new CustomException(DeliveryRouteErrorCode.INVALID_ROUTE_TIME);
        }
    }
}