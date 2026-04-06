package org.sixpang.deliveryservice.application.dto;

import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerSearchCondition(
        UUID hubId,
        DeliveryManagerType type,
        UUID userId
) {
}