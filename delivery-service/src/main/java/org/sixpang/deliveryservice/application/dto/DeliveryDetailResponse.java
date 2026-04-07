package org.sixpang.deliveryservice.application.dto;

import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryDetailResponse(
        UUID id,
        UUID orderId,
        DeliveryStatus status,
        UUID departureHub,
        UUID arrivalHub,
        String address,
        UUID receiverId,
        UUID deliveryManagerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DeliveryDetailResponse from(Delivery delivery) {
        return new DeliveryDetailResponse(
                delivery.getId(),
                delivery.getOrderId(),
                delivery.getStatus(),
                delivery.getDepartureHub(),
                delivery.getArrivalHub(),
                delivery.getAddress(),
                delivery.getReceiverId(),
                delivery.getDeliveryManagerId(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        );
    }
}