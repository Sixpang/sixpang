package org.sixpang.deliveryservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubDeliveryManagerRepositoryImpl implements HubDeliveryManagerJpaRepository {
    private final HubDeliveryManagerJpaRepository hubDeliveryManagerJpaRepository;

    boolean existsByUserId(UUID userId) {
        return hubDeliveryManagerJpaRepository.existsByUserId(userId);
    }

    //전국 WAIT 중 순번 가장 낮은 사람
    Optional<HubDeliveryManager> findTopByStatusOrderByDeliverySequenceAsc(
            DeliveryManagerStatus status) {
        return hubDeliveryManagerJpaRepository.findTopByStatusOrderByDeliverySequenceAsc(status);
    }

    int countByDeletedAtIsNull() {
        return  hubDeliveryManagerJpaRepository.countByDeletedAtIsNull();
    } // 전국 10명 제한

    List<HubDeliveryManager> findAllByStatus(DeliveryManagerStatus status) {
        return  hubDeliveryManagerJpaRepository.findAllByStatus(status);
    }

}
