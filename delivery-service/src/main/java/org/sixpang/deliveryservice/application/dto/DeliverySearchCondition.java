package org.sixpang.deliveryservice.application.dto;

import java.util.UUID;

public record DeliverySearchCondition(
        UUID hubId,
        String status,
        UUID receiverId
) {
}