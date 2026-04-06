package org.sixpang.deliveryservice.domain.repository;

import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HubDeliveryManagerRepository {
    boolean existsByUserId(UUID userId);

    //전국 WAIT 중 순번 가장 낮은 사람
    Optional<HubDeliveryManager> findTopByStatusOrderByDeliverySequenceAsc(
            DeliveryManagerStatus status);

    int countByDeletedAtIsNull(); // 전국 10명 제한

    List<HubDeliveryManager> findAllByStatus(DeliveryManagerStatus status);

    HubDeliveryManager save(HubDeliveryManager manager);

    Optional<HubDeliveryManager> findById(UUID userId);

    Page<HubDeliveryManager> findAllByDeletedAtIsNull(Pageable pageable);
}
