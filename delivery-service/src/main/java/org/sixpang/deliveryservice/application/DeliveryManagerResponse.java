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
        UUID hubId,         // 6번째 인수
        LocalDateTime createdAt // 7번째 인수
) {
    public static DeliveryManagerResponse fromHub(HubDeliveryManager entity) {
        return new DeliveryManagerResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getDeliverySequence(),
                entity.getStatus(),
                DeliveryManagerType.HUB,
                null,               // 허브 매니저는 소속 허브가 없으므로 null 전달 (6번째)
                entity.getCreatedAt() // (7번째)
        );
    }

    public static DeliveryManagerResponse fromCompany(CompanyDeliveryManager entity) {
        return new DeliveryManagerResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getDeliverySequence(),
                entity.getStatus(),
                DeliveryManagerType.COMPANY,
                entity.getHubId(),    // 업체 매니저는 허브 ID 전달 (6번째)
                entity.getCreatedAt() // (7번째)
        );
    }
}