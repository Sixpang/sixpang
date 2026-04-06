package org.sixpang.deliveryservice.application.dto;

import org.sixpang.deliveryservice.domain.model.entity.Delivery;

import java.math.BigDecimal;
import java.util.UUID;

public record DeliveryRouteCreateCommand(Delivery deliveryId, Integer hubSequence, UUID departureHub, UUID arrivalHub,
                                         UUID hubRouteId, BigDecimal estimatedDistance, Long estimatedTime,
                                         UUID hubDeliveryManagerId) {
}
