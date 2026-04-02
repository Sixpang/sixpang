package org.sixpang.deliveryservice.application;

import org.sixpang.deliveryservice.domain.model.entity.DeliveryBaseEntity;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryManagerResponse(
        UUID id,
        UUID userId,
        int deliverySequence,
        DeliveryManagerStatus status,
        DeliveryManagerType type,
        UUID hubId,         // HUB 타입만 값 있음
        LocalDateTime createdAt
) {
    public static DeliveryManagerResponse from(DeliveryBaseEntity entity) {
        UUID hubId = entity instanceof HubDeliveryManager hub ? hub.getHubId() : null;
        DeliveryManagerType type = entity instanceof HubDeliveryManager
                ? DeliveryManagerType.HUB : DeliveryManagerType.COMPANY;
        return new DeliveryManagerResponse(
                entity.getId(), entity.getUserId(), entity.getDeliverySequence(),
                entity.getStatus(), type, hubId, entity.getCreatedAt()
        );
    }
}