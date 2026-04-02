package org.sixpang.deliveryservice.application;

import org.sixpang.deliveryservice.domain.model.entity.CompanyDeliveryManager;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;

import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryManagerResponse(
        UUID id,
        UUID userId,
        Integer deliverySequence,
        DeliveryManagerStatus status,
        DeliveryManagerType type,
        UUID hubId,         // HUB 타입만 값 있음
        LocalDateTime createdAt
) {
    public static DeliveryManagerResponse fromHub(HubDeliveryManager entity) {
        return new DeliveryManagerResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getDeliverySequence(),
                entity.getStatus(),
                DeliveryManagerType.HUB,
                entity.getHubId(),
                entity.getCreatedAt()
        );
    }

    public static DeliveryManagerResponse fromCompany(CompanyDeliveryManager entity) {
        return new DeliveryManagerResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getDeliverySequence(),
                entity.getStatus(),
                DeliveryManagerType.COMPANY,
                null,  // hubId 없음
                entity.getCreatedAt()
        );
    }

}