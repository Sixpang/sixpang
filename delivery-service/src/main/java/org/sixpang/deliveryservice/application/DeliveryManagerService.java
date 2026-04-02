package org.sixpang.deliveryservice.application;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.domain.model.entity.CompanyDeliveryManager;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;
import org.sixpang.deliveryservice.domain.repository.CompanyDeliveryManagerRepository;
import org.sixpang.deliveryservice.domain.repository.HubDeliveryManagerRepository;
import org.sixpang.deliveryservice.infrastructure.client.HubClient;
import org.sixpang.deliveryservice.infrastructure.client.UserClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerService {
    private final HubDeliveryManagerRepository hubDeliveryManagerRepository;
    private final CompanyDeliveryManagerRepository companyDeliveryManagerRepository;
    private final UserClient userClient;
    private final HubClient hubClient;

    private static final int HUB_MANAGER_TOTAL_MAX = 10;
    private static final int COMPANY_MANAGER_PER_HUB_MAX = 10;

    //배송 담당자 생성
    //role부분 인증/인가 처리되면 수정하기
    public DeliveryManagerResponse create(DeliveryManagerCreateRequest request, String role, UUID requestUserId) {
        validateCreatePermission(role, request.type());
        validateUserExists(request.userId());

        if (hubDeliveryManagerRepository.existsByUserId(request.userId()) ||
            companyDeliveryManagerRepository.existsByUserId(request.userId())) {
            throw new IllegalStateException("이미 배송 담당자로 등록된 유저");
        }
        return request.type() == DeliveryManagerType.HUB
                ? createHubManager(request)
                : createCompanyManager(request);
    }

    //허브 배송 담당자 생성
    private DeliveryManagerResponse createHubManager(DeliveryManagerCreateRequest request){
        if (hubDeliveryManagerRepository.countByDeletedAtIsNull() >= HUB_MANAGER_TOTAL_MAX)
            throw new IllegalStateException("허브 배송 담당자 정원 초과");

        HubDeliveryManager manager = HubDeliveryManager.create(request.userId());
        return DeliveryManagerResponse.fromHub(hubDeliveryManagerRepository.save(manager));
    }

    //업체 배송 담당자 생성
    private DeliveryManagerResponse createCompanyManager(DeliveryManagerCreateRequest request){
        if (request.hubId() == null)
            throw new IllegalArgumentException("업체 배송 담당자는 허브 ID가 필요합니다.");

        validateHubExists(request.hubId());

        if (companyDeliveryManagerRepository.countByHubIdAndDeletedAtIsNull(request.hubId()) >= COMPANY_MANAGER_PER_HUB_MAX)
            throw new IllegalStateException("해당 허브의 업체 배송 담당자 정원 초과");

        CompanyDeliveryManager manager = CompanyDeliveryManager.create(request.userId(), request.hubId());
        return DeliveryManagerResponse.fromCompany(companyDeliveryManagerRepository.save(manager));
    }

    //조회
    @Transactional(readOnly = true)
    public DeliveryManagerResponse getById(UUID managerId, DeliveryManagerType type, String role, UUID requestUserId) {
        if (type == DeliveryManagerType.HUB){
            HubDeliveryManager manager = findHubManagerOrThrow(managerId);
            validateReadPermission(role, requestUserId, manager.getUserId());
            return DeliveryManagerResponse.fromHub(manager);
        }else{
            CompanyDeliveryManager manager = findCompanyManagerOrThrow(managerId);
            validateReadPermission(role, requestUserId, manager.getUserId());
            return DeliveryManagerResponse.fromCompany(manager);
        }
    }

    //목록 조회
    @Transactional(readOnly = true)
    public Page<DeliveryManagerResponse> search(DeliveryManagerSearchCondition condition, Pageable pageable, String role, UUID requestUserId) {
        throw new UnsupportedOperationException("예외");
    }

    //배송 담당자 수정
    public DeliveryManagerResponse update(UUID managerId, DeliveryManagerType type,
                                          DeliveryManagerUpdateRequest request,
                                          String role, UUID requestUserId, UUID requestHubId) {
        //==================================================================================================
        if (type == DeliveryManagerType.HUB) {
            HubDeliveryManager manager = findHubManagerOrThrow(managerId);
            validateModifyPermission(role, requestUserId, requestHubId, null);
            if (request.status() != null) manager.updateStatus(request.status());
            return DeliveryManagerResponse.fromHub(hubDeliveryManagerRepository.save(manager));

        } else {
            CompanyDeliveryManager manager = findCompanyManagerOrThrow(managerId);
            validateModifyPermission(role, requestUserId, requestHubId, manager.getHubId());
            if (request.status() != null) manager.updateStatus(request.status());
            return DeliveryManagerResponse.fromCompany(companyDeliveryManagerRepository.save(manager));
        }
    }

    // ──────────────── 삭제 ────────────────
    public void delete(UUID managerId, DeliveryManagerType type,
                       String role, UUID requestUserId, UUID requestHubId) {
        if (type == DeliveryManagerType.HUB) {
            HubDeliveryManager manager = findHubManagerOrThrow(managerId);
            validateModifyPermission(role, requestUserId, requestHubId, null);
            manager.setDeletedAt(LocalDateTime.now());
            manager.setDeletedBy(requestUserId);
            hubDeliveryManagerRepository.save(manager);
        } else {
            CompanyDeliveryManager manager = findCompanyManagerOrThrow(managerId);
            validateModifyPermission(role, requestUserId, requestHubId, manager.getHubId());
            manager.softDelete(requestUserId);
            companyDeliveryManagerRepository.save(manager);
        }
    }

    // ──────────────── 배정 (DeliveryService에서 호출) ────────────────
    public HubDeliveryManager assignHubManager() {
        HubDeliveryManager manager = hubDeliveryManagerRepository
                .findTopByStatusOrderByDeliverySequenceAsc(DeliveryManagerStatus.WAIT)
                .orElseThrow(() -> new IllegalStateException("배정 가능한 허브 배송 담당자가 없습니다."));
        manager.updateStatus(DeliveryManagerStatus.ON_TASK);
        return hubDeliveryManagerRepository.save(manager);
    }

    public CompanyDeliveryManager assignCompanyManager(UUID hubId) {
        CompanyDeliveryManager manager = companyDeliveryManagerRepository
                .findTopByHubIdAndStatusOrderByDeliverySequenceAsc(hubId, DeliveryManagerStatus.WAIT)
                .orElseThrow(() -> new IllegalStateException("배정 가능한 업체 배송 담당자가 없습니다."));
        manager.updateStatus(DeliveryManagerStatus.ON_TASK);
        return companyDeliveryManagerRepository.save(manager);
    }

    // ──────────────── 외부 서비스 검증 ────────────────
    // TODO: 공통 예외 처리 확정 후 CustomException으로 수정
    private void validateUserExists(UUID userId) {
        try {
            userClient.checkExists(userId);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
    }

    private void validateHubExists(UUID hubId) {
        try {
            hubClient.checkExists(hubId);
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("존재하지 않는 허브입니다.");
        }
    }

    // ──────────────── 권한 검증 ────────────────
    private void validateCreatePermission(String role, DeliveryManagerType type) {
        if ("MASTER".equals(role)) return;
        if ("HUB_MANAGER".equals(role) && type == DeliveryManagerType.COMPANY) return;
        throw new IllegalArgumentException("생성 권한이 없습니다.");
    }

    private void validateReadPermission(String role, UUID requestUserId, UUID managerUserId) {
        if ("MASTER".equals(role) || "HUB_MANAGER".equals(role)) return;
        if ("DELIVERY_MANAGER".equals(role) && managerUserId.equals(requestUserId)) return;
        throw new IllegalArgumentException("조회 권한이 없습니다.");
    }

    private void validateModifyPermission(String role, UUID requestUserId,
                                          UUID requestHubId, UUID managerHubId) {
        if ("MASTER".equals(role)) return;
        if ("HUB_MANAGER".equals(role)
                && requestHubId != null
                && requestHubId.equals(managerHubId)) return;
        throw new IllegalArgumentException("수정/삭제 권한이 없습니다.");
    }

    // ──────────────── 공통 조회 ────────────────
    private HubDeliveryManager findHubManagerOrThrow(UUID id) {
        return hubDeliveryManagerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 허브 배송 담당자입니다."));
    }

    private CompanyDeliveryManager findCompanyManagerOrThrow(UUID id) {
        return companyDeliveryManagerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 업체 배송 담당자입니다."));
    }

/*
    //공통 예외 처리 얘기해보고 수정해야함
    private void validateUserExists(UUID userId){
        try{
            userClient.checkExists(userId);
        }catch(FeignException.NotFound e){
            throw new IllegalArgumentException("유저 없음");
        }
    }
    private void validateHubExists(UUID hubId){
        try{
            hubClient.checkExists((hubId));
        }catch(FeignException.NotFound e){
            throw new IllegalArgumentException("허브 없음");
        }
    }
    */
}