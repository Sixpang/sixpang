package org.sixpang.deliveryservice.application.dto;

import java.math.BigDecimal;

public record HubRouteInfo(BigDecimal estimatedDistance, // km
                           Long estimatedTime) {
}
