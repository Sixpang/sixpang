package org.sixpang.deliveryservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.sixpang.deliveryservice.domain.repository.HubDeliveryManagerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HubDeliveryManagerRepositoryImpl implements HubDeliveryManagerRepository {
    private final HubDeliveryManagerJpaRepository hubDeliveryManagerJpaRepository;

    @Override
    public boolean existsByUserId(UUID userId) {
        return hubDeliveryManagerJpaRepository.existsByUserId(userId);
    }

    //전국 WAIT 중 순번 가장 낮은 사람
    @Override
    public Optional<HubDeliveryManager> findTopByStatusOrderByDeliverySequenceAsc(
            DeliveryManagerStatus status) {
        return hubDeliveryManagerJpaRepository.findTopByStatusOrderByDeliverySequenceAsc(status);
    }

    @Override
    public int countByDeletedAtIsNull() {
        return  hubDeliveryManagerJpaRepository.countByDeletedAtIsNull();
    } // 전국 10명 제한

    @Override
    public List<HubDeliveryManager> findAllByStatus(DeliveryManagerStatus status) {
        return  hubDeliveryManagerJpaRepository.findAllByStatus(status);
    }

    @Override
    public HubDeliveryManager save(HubDeliveryManager manager){
        return hubDeliveryManagerJpaRepository.save(manager);
    }

    @Override
    public Optional<HubDeliveryManager> findById(UUID userId){
        return hubDeliveryManagerJpaRepository.findById(userId);
    }

}