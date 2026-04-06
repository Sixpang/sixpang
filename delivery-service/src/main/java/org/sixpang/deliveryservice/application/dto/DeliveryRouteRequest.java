package org.sixpang.deliveryservice.application.dto;

import java.util.UUID;

public record DeliveryRouteRequest(UUID deliveryId,
                                   Integer hubSequence,
                                   UUID departureHub,
                                   UUID arrivalHub,
                                   UUID hubDeliveryManagerId) {
}
