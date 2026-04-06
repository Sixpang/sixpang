package org.sixpang.deliveryservice.application.dto;

import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;

import java.util.UUID;

public record DeliveryManagerUpdateRequest(DeliveryManagerStatus status, UUID hubId) {

}
