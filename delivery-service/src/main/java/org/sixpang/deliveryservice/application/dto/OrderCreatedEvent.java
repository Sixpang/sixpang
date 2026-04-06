package org.sixpang.deliveryservice.application.dto;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID departureHub,
        UUID arrivalHub,
        String address,
        UUID receiverId,
        UUID supplierId
) {
}