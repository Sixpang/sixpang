package org.sixpang.deliveryservice.application;

import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;

import java.util.UUID;

public record DeliveryManagerUpdateRequest(DeliveryManagerStatus status, UUID hubId) {
}
