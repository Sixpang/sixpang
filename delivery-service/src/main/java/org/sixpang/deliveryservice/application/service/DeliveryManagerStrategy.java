package org.sixpang.deliveryservice.application.service;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerSearchCondition;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerUpdateRequest;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DeliveryManagerStrategy {
    boolean supports(DeliveryManagerType type);

    DeliveryManagerResponse get(UUID id);

    DeliveryManagerResponse update(UUID id, DeliveryManagerUpdateRequest request, String role, UUID userId, UUID hubId);

    Page<DeliveryManagerResponse> search(DeliveryManagerSearchCondition condition,
                                         Pageable pageable, String role, UUID requestUserId);

    default void validateModifyPermission(String role, UUID requestHubId, UUID managerHubId) {
        if ("MASTER".equals(role)) return;

        if ("HUB_MANAGER".equals(role)) {
            if (managerHubId != null && managerHubId.equals(requestHubId)) {
                return;
            }
        }
        throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
    }
}
