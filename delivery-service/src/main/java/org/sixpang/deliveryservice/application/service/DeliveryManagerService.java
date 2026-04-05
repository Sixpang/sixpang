package org.sixpang.deliveryservice.application.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerCreateRequest;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerSearchCondition;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerUpdateRequest;
import org.sixpang.deliveryservice.domain.model.entity.CompanyDeliveryManager;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;
import org.sixpang.deliveryservice.domain.repository.CompanyDeliveryManagerRepository;
import org.sixpang.deliveryservice.domain.repository.HubDeliveryManagerRepository;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_HUB_KEY_PREFIX = "delivery:managers:hub:";

    private final HubDeliveryManagerRepository hubDeliveryManagerRepository;
    private final CompanyDeliveryManagerRepository companyDeliveryManagerRepository;
    private final DeliveryUserClient deliveryUserClient;
    private final DeliveryHubClient deliveryHubClient;

    private static final int HUB_MANAGER_TOTAL_MAX = 10;
    private static final int COMPANY_MANAGER_PER_HUB_MAX = 10;

    //배송 담당자 생성
    //수정예약:role부분 인증/인가 처리되면 수정하기
    public DeliveryManagerResponse create(DeliveryManagerCreateRequest request, String role, UUID requestUserId) {
        validateCreatePermission(role, request.type());

        validateUserExists(request.userId());

        if (hubDeliveryManagerRepository.existsByUserId(request.userId()) ||
                companyDeliveryManagerRepository.existsByUserId(request.userId())) {
            throw new CustomException(DeliveryErrorCode.ALREADY_EXISTS_MANAGER);
        }
        return request.type() == DeliveryManagerType.HUB
                ? createHubManager(request)
                : createCompanyManager(request);
    }

    //허브 배송 담당자 생성
    private DeliveryManagerResponse createHubManager(DeliveryManagerCreateRequest request) {
        if (hubDeliveryManagerRepository.countByDeletedAtIsNull() >= HUB_MANAGER_TOTAL_MAX)
            throw new CustomException(DeliveryErrorCode.MANAGER_LIMIT_EXCEEDED);

        HubDeliveryManager manager = HubDeliveryManager.create(request.userId());
        return DeliveryManagerResponse.fromHub(hubDeliveryManagerRepository.save(manager));
    }

    //업체 배송 담당자 생성
    private DeliveryManagerResponse createCompanyManager(DeliveryManagerCreateRequest request) {
        if (request.hubId() == null)
            throw new CustomException(DeliveryErrorCode.HUB_ID_REQUIRED);

        validateHubExists(request.hubId());

        if (companyDeliveryManagerRepository.countByHubIdAndDeletedAtIsNull(request.hubId()) >= COMPANY_MANAGER_PER_HUB_MAX) {
            throw new CustomException(DeliveryErrorCode.MANAGER_LIMIT_EXCEEDED);
        }

        CompanyDeliveryManager manager = CompanyDeliveryManager.create(request.userId(), request.hubId());
        return DeliveryManagerResponse.fromCompany(companyDeliveryManagerRepository.save(manager));
    }

    //조회
    @Transactional(readOnly = true)
    public DeliveryManagerResponse getById(UUID managerId, DeliveryManagerType type, String role, UUID requestUserId) {

        if (type == DeliveryManagerType.HUB) {
            HubDeliveryManager manager = findHubManagerOrThrow(managerId);
            validateReadPermission(role, requestUserId, manager.getUserId());
            return DeliveryManagerResponse.fromHub(manager);
        } else {
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

    //삭제
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

    public HubDeliveryManager assignHubManager() {
        // 실제로는 '전체 허브 담당자' 키를 사용하거나 로직에 맞게 키를 정하세요.
        String redisKey = REDIS_HUB_KEY_PREFIX + "all";

        // 1. Redis에서 순서대로 ID 하나 가져오기 (오른쪽에서 꺼내서 왼쪽으로 다시 넣음 -> 순환)
        String managerIdStr = redisTemplate.opsForList()
                .rightPopAndLeftPush(redisKey, redisKey);

        if (managerIdStr == null) {
            // Redis에 데이터가 없으면 DB에서 WAIT 상태인 애들을 긁어와서 채워주는 로직이 필요함
            refreshRedisCache(redisKey);
            managerIdStr = redisTemplate.opsForList().rightPopAndLeftPush(redisKey, redisKey);

            if (managerIdStr == null) throw new CustomException(DeliveryErrorCode.MANAGER_NOT_FOUND);
        }

        // 2. DB에서 엔티티 조회 및 상태 변경
        HubDeliveryManager manager = hubDeliveryManagerRepository.findById(UUID.fromString(managerIdStr))
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.MANAGER_NOT_FOUND));

        manager.updateStatus(DeliveryManagerStatus.ON_TASK);
        return hubDeliveryManagerRepository.save(manager);
    }

    // Redis 캐시가 비었을 때 DB 데이터로 채워주는 헬퍼 메서드
    private void refreshRedisCache(String key) {
        List<HubDeliveryManager> waiters = hubDeliveryManagerRepository.findAllByStatus(DeliveryManagerStatus.WAIT);
        for (HubDeliveryManager m : waiters) {
            redisTemplate.opsForList().leftPush(key, m.getId().toString());
        }
    }

    public CompanyDeliveryManager assignCompanyManager(UUID hubId) {
        String redisKey = "delivery:managers:company:" + hubId.toString();

        //Redis에서 담당자 ID 하나를 완전히 꺼내기 (RPOPLPUSH 대신 rightPop 사용)
        //배정된 사람은 다시 큐에 넣지 않아야 다른 사람이 배정
        String managerIdStr = redisTemplate.opsForList().rightPop(redisKey);

        //Redis가 비어있다면 DB에서 해당 허브의 'WAIT' 상태인 담당자들을 로딩
        if (managerIdStr == null) {
            List<CompanyDeliveryManager> managers = companyDeliveryManagerRepository
                    .findAllByHubIdAndStatus(hubId, DeliveryManagerStatus.WAIT);

            if (managers.isEmpty()) {
                throw new CustomException(DeliveryErrorCode.MANAGER_LIMIT_EXCEEDED);
            }

            //DB에서 가져온 대기자들 Redis에서 넣기
            for (CompanyDeliveryManager m : managers) {
                redisTemplate.opsForList().leftPush(redisKey, m.getId().toString());
            }

            //적재 후 다시 하나 꺼내기
            managerIdStr = redisTemplate.opsForList().rightPop(redisKey);
        }

        //DB 상태 업데이트 및 반환
        CompanyDeliveryManager manager = findCompanyManagerOrThrow(UUID.fromString(managerIdStr));

        //이미 업무 중인지 한 번 더 검증
        if (manager.getStatus() != DeliveryManagerStatus.WAIT) {
            //만약 누군가 가로챘다면 재귀 호출로 다음 사람 찾기
            return assignCompanyManager(hubId);
        }

        manager.updateStatus(DeliveryManagerStatus.ON_TASK);
        return companyDeliveryManagerRepository.save(manager);
    }

    //외부 서비스 검증
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

    //권한 검증
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

    //공통 조회
    private HubDeliveryManager findHubManagerOrThrow(UUID id) {
        return hubDeliveryManagerRepository.findById(id)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.MANAGER_NOT_FOUND));
    }

    private CompanyDeliveryManager findCompanyManagerOrThrow(UUID id) {
        return companyDeliveryManagerRepository.findById(id)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.MANAGER_NOT_FOUND));
    }
}