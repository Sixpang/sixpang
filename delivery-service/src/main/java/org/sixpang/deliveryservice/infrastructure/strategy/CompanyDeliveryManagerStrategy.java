package org.sixpang.deliveryservice.infrastructure.strategy;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerSearchCondition;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerUpdateRequest;
import org.sixpang.deliveryservice.application.service.strategy.DeliveryManagerStrategy;
import org.sixpang.deliveryservice.domain.model.entity.CompanyDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;
import org.sixpang.deliveryservice.domain.repository.CompanyDeliveryManagerRepository;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompanyDeliveryManagerStrategy implements DeliveryManagerStrategy {
    private final CompanyDeliveryManagerRepository repository;

    @Override
    public boolean supports(DeliveryManagerType type) {
        return type == DeliveryManagerType.COMPANY;
    }

    public DeliveryManagerResponse get(UUID id) {
        CompanyDeliveryManager manager = repository.findById(id)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.MANAGER_NOT_FOUND));

        return DeliveryManagerResponse.fromCompany(manager);
    }

    @Override
    public Page<DeliveryManagerResponse> search(DeliveryManagerSearchCondition condition,
                                                Pageable pageable, String role, UUID requestUserId) {
        Page<CompanyDeliveryManager> managers;

        if (condition.hubId() != null) {
            managers = repository.findAllByHubIdAndDeletedAtIsNull(condition.hubId(), pageable);
        } else {
            managers = repository.findAllByDeletedAtIsNull(pageable);
        }

        return managers.map(DeliveryManagerResponse::fromCompany);
    }

    @Override
    public DeliveryManagerResponse update(UUID id, DeliveryManagerUpdateRequest request, String role, UUID userId, UUID requestHubId) {
        CompanyDeliveryManager manager = repository.findById(id)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.MANAGER_NOT_FOUND));

        if (manager.getHubId() == null) {
            throw new CustomException(DeliveryErrorCode.HUB_ID_REQUIRED);
        }

        validateModifyPermission(role, requestHubId, manager.getHubId());

        if (request.status() != null) manager.updateStatus(request.status());
        if (request.hubId() != null) manager.updateHubId(request.hubId());

        return DeliveryManagerResponse.fromCompany(manager);
    }
}
