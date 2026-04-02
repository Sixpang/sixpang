package org.sixpang.deliveryservice.domain.repository;

import org.sixpang.deliveryservice.domain.model.entity.CompanyDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyDeliveryManagerRepository extends JpaRepository<CompanyDeliveryManager, UUID> {
    boolean existsByUserId(UUID userId);

    //해당 허브 소속 WAIT 중 순번 가장 낮은 사람
    Optional<CompanyDeliveryManager> findTopByHubIdAndStatusOrderByDeliverySequenceAsc(
            UUID hubId, DeliveryManagerStatus status);

    // 허브당 10명 제한 체크
    int countByHubIdAndDeletedAtIsNull(UUID hubId);
}