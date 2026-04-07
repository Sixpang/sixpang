package org.sixpang.deliveryservice.application.service.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerCreateRequest;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerSearchCondition;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerUpdateRequest;
import org.sixpang.deliveryservice.application.service.DeliveryManagerAssigner;
import org.sixpang.deliveryservice.application.service.client.DeliveryHubClient;
import org.sixpang.deliveryservice.application.service.client.DeliveryUserClient;
import org.sixpang.deliveryservice.application.service.strategy.DeliveryManagerStrategy;
import org.sixpang.deliveryservice.domain.model.entity.CompanyDeliveryManager;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;
import org.sixpang.deliveryservice.domain.repository.CompanyDeliveryManagerRepository;
import org.sixpang.deliveryservice.domain.repository.HubDeliveryManagerRepository;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.sixpang.deliveryservice.exception.DeliveryManagerErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerService {

    private final HubDeliveryManagerRepository hubDeliveryManagerRepository;
    private final CompanyDeliveryManagerRepository companyDeliveryManagerRepository;
    private final DeliveryUserClient deliveryUserClient;
    private final DeliveryHubClient deliveryHubClient;

    private final List<DeliveryManagerStrategy> strategies;
    private final DeliveryManagerAssigner assigner; // DIP 적용된 배정 인터페이스

    private static final int HUB_MANAGER_TOTAL_MAX = 10;
    private static final int COMPANY_MANAGER_PER_HUB_MAX = 10;

    public DeliveryManagerResponse create(DeliveryManagerCreateRequest request, String role, UUID requestUserId) {
        validateCreatePermission(role, request.type());
        validateUserExists(request.userId());

        if (hubDeliveryManagerRepository.existsByUserId(request.userId()) ||
                companyDeliveryManagerRepository.existsByUserId(request.userId())) {
            throw new CustomException(DeliveryManagerErrorCode.ALREADY_EXISTS_MANAGER);
        }

        if (request.type() == DeliveryManagerType.HUB) {
            return createHubManager(request);
        }
        return createCompanyManager(request);
    }

    private DeliveryManagerResponse createHubManager(DeliveryManagerCreateRequest request) {
        if (isHubManagerLimitExceeded())
            throw new CustomException(DeliveryManagerErrorCode.MANAGER_LIMIT_EXCEEDED);

        HubDeliveryManager manager = HubDeliveryManager.create(request.userId());
        return DeliveryManagerResponse.fromHub(hubDeliveryManagerRepository.save(manager));
    }

    private DeliveryManagerResponse createCompanyManager(DeliveryManagerCreateRequest request) {
        if (request.hubId() == null)
            throw new CustomException(DeliveryManagerErrorCode.HUB_ID_REQUIRED);

        validateHubExists(request.hubId());
        if (isCompanyManagerLimitExceeded(request.hubId()))
            throw new CustomException(DeliveryManagerErrorCode.MANAGER_LIMIT_EXCEEDED);

        CompanyDeliveryManager manager = CompanyDeliveryManager.create(request.userId(), request.hubId());
        return DeliveryManagerResponse.fromCompany(companyDeliveryManagerRepository.save(manager));
    }

    @Transactional(readOnly = true)
    public DeliveryManagerResponse getById(UUID managerId, DeliveryManagerType type, String role, UUID requestUserId) {
        DeliveryManagerStrategy strategy = findStrategy(type);
        DeliveryManagerResponse response = strategy.get(managerId);

        validateReadPermission(role, requestUserId, response.userId());
        return response;
    }

    public DeliveryManagerResponse update(UUID managerId, DeliveryManagerType type,
                                          DeliveryManagerUpdateRequest request,
                                          String role, UUID requestUserId, UUID requestHubId) {
        return findStrategy(type).update(managerId, request, role, requestUserId, requestHubId);
    }

    @Transactional(readOnly = true)
    public Page<DeliveryManagerResponse> search(DeliveryManagerSearchCondition condition, Pageable pageable, String role, UUID requestUserId) {
        return findStrategy(condition.type()).search(condition, pageable, role, requestUserId);
    }

    public void delete(UUID managerId, DeliveryManagerType type,
                       String role, UUID requestUserId, UUID requestHubId) {
        if (type == DeliveryManagerType.HUB) {
            HubDeliveryManager manager = findHubManagerOrThrow(managerId);
            validateModifyPermission(role, requestUserId, requestHubId, null);
            manager.softDelete(requestUserId);
            hubDeliveryManagerRepository.save(manager);
        } else {
            CompanyDeliveryManager manager = findCompanyManagerOrThrow(managerId);
            validateModifyPermission(role, requestUserId, requestHubId, manager.getHubId());
            manager.softDelete(requestUserId);
            companyDeliveryManagerRepository.save(manager);
        }
    }

    private DeliveryManagerStrategy findStrategy(DeliveryManagerType type) {
        return this.strategies.stream()
                .filter(s -> s.supports(type))
                .findFirst()
                .orElseThrow(() -> new CustomException(DeliveryManagerErrorCode.UNSUPPORTED_TYPE));
    }

    //배송 담당자 배정 로직
    public HubDeliveryManager assignHubManager() {
        String key = "delivery:hub:all";

        String id = assigner.getNextIdWithRotation(key)
                .orElseGet(() -> {
                    refreshHubCache(key);
                    return assigner.getNextIdWithRotation(key)
                            .orElseThrow(() -> new CustomException(DeliveryManagerErrorCode.MANAGER_NOT_FOUND));
                });

        HubDeliveryManager manager = hubDeliveryManagerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new CustomException(DeliveryManagerErrorCode.MANAGER_NOT_FOUND));

        manager.updateStatus(DeliveryManagerStatus.ON_TASK);
        return manager;
    }

    public CompanyDeliveryManager assignCompanyManager(UUID hubId) {
        String key = "delivery:company:" + hubId;

        String id = assigner.getNextId(key)
                .orElseGet(() -> {
                    refreshCompanyCache(hubId, key);
                    return assigner.getNextId(key)
                            .orElseThrow(() -> new CustomException(DeliveryManagerErrorCode.MANAGER_LIMIT_EXCEEDED));
                });

        CompanyDeliveryManager manager = companyDeliveryManagerRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new CustomException(DeliveryManagerErrorCode.MANAGER_NOT_FOUND));

        manager.updateStatus(DeliveryManagerStatus.ON_TASK);
        return manager;
    }

    // --- 내부 검증 및 헬퍼 메서드 ---

    private boolean isHubManagerLimitExceeded() {
        return hubDeliveryManagerRepository.countByDeletedAtIsNull() >= HUB_MANAGER_TOTAL_MAX;
    }

    private boolean isCompanyManagerLimitExceeded(UUID hudId) {
        return companyDeliveryManagerRepository.countByHubIdAndDeletedAtIsNull(hudId) >= COMPANY_MANAGER_PER_HUB_MAX;
    }

    private void validateUserExists(UUID userId) {
        try {
            deliveryUserClient.checkExists(userId);
        } catch (FeignException.NotFound e) {
            throw new CustomException(DeliveryErrorCode.USER_NOT_FOUND);
        }
    }

    private void validateHubExists(UUID hubId) {
        try {
            deliveryHubClient.checkExists(hubId);
        } catch (FeignException.NotFound e) {
            throw new CustomException(DeliveryErrorCode.HUB_NOT_FOUND);
        }
    }

    private void refreshHubCache(String key) {
        List<HubDeliveryManager> waiters = hubDeliveryManagerRepository.findAllByStatus(DeliveryManagerStatus.WAIT);
        List<String> ids = waiters.stream()
                .map(m -> m.getId().toString())
                .toList();

        if (ids.isEmpty()) {
            throw new CustomException(DeliveryManagerErrorCode.MANAGER_NOT_FOUND);
        }

        assigner.refreshCache(key, ids);
    }

    private void refreshCompanyCache(UUID hubId, String key) {
        List<CompanyDeliveryManager> waiters = companyDeliveryManagerRepository.findAllByHubIdAndStatus(hubId, DeliveryManagerStatus.WAIT);
        List<String> ids = waiters.stream()
                .map(m -> m.getId().toString())
                .toList();

        if (ids.isEmpty()) {
            throw new CustomException(DeliveryManagerErrorCode.MANAGER_LIMIT_EXCEEDED);
        }

        assigner.refreshCache(key, ids);
    }

    private void validateCreatePermission(String role, DeliveryManagerType type) {
        if ("MASTER".equals(role)) return;
        if ("HUB_MANAGER".equals(role) && type == DeliveryManagerType.COMPANY) return;
        throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
    }

    private void validateReadPermission(String role, UUID requestUserId, UUID managerUserId) {
        if ("MASTER".equals(role) || "HUB_MANAGER".equals(role)) return;
        if ("DELIVERY_MANAGER".equals(role) && managerUserId.equals(requestUserId)) return;
        throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
    }

    private void validateModifyPermission(String role, UUID requestUserId,
                                          UUID requestHubId, UUID managerHubId) {
        if ("MASTER".equals(role)) return;
        if ("HUB_MANAGER".equals(role)
                && requestHubId != null
                && requestHubId.equals(managerHubId)) return;
        throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
    }

    private HubDeliveryManager findHubManagerOrThrow(UUID id) {
        return hubDeliveryManagerRepository.findById(id)
                .orElseThrow(() -> new CustomException(DeliveryManagerErrorCode.MANAGER_NOT_FOUND));
    }

    private CompanyDeliveryManager findCompanyManagerOrThrow(UUID id) {
        return companyDeliveryManagerRepository.findById(id)
                .orElseThrow(() -> new CustomException(DeliveryManagerErrorCode.MANAGER_NOT_FOUND));
    }
}