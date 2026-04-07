package org.sixpang.deliveryservice.application.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record HubRouteInfo(List<UUID> hubPath,
                           BigDecimal estimatedDistance, // km
                           Long estimatedTime) {
}
