package org.sixpang.deliveryservice.infrastructure.strategy;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerSearchCondition;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerUpdateRequest;
import org.sixpang.deliveryservice.application.service.strategy.DeliveryManagerStrategy;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;
import org.sixpang.deliveryservice.domain.repository.HubDeliveryManagerRepository;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubDeliveryManagerStrategy implements DeliveryManagerStrategy {
    private final HubDeliveryManagerRepository repository;

    @Override
    public boolean supports(DeliveryManagerType type) {
        return type == DeliveryManagerType.HUB;
    }

    @Override
    public DeliveryManagerResponse get(UUID id) {
        HubDeliveryManager manager = repository.findById(id)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.MANAGER_NOT_FOUND));

        return DeliveryManagerResponse.fromHub(manager);
    }

    @Override
    public Page<DeliveryManagerResponse> search(DeliveryManagerSearchCondition condition,
                                                Pageable pageable, String role, UUID requestUserId) {

        Page<HubDeliveryManager> managers;

        if (condition.hubId() != null) {
            managers = repository.findAllByDeletedAtIsNull(pageable);
        } else {
            managers = repository.findAllByDeletedAtIsNull(pageable);
        }

        return managers.map(DeliveryManagerResponse::fromHub);
    }

    @Override
    public DeliveryManagerResponse update(UUID id, DeliveryManagerUpdateRequest request, String role, UUID userId, UUID requestHubId) {
        HubDeliveryManager manager = repository.findById(id)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.MANAGER_NOT_FOUND));

        validateModifyPermission(role, requestHubId, null);

        manager.updateStatus(request.status());
        return DeliveryManagerResponse.fromHub(manager);
    }
}
