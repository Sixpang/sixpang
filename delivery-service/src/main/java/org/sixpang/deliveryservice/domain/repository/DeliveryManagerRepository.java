package org.sixpang.deliveryservice.domain.repository;

import org.sixpang.deliveryservice.domain.model.entity.CompanyDeliveryManger;
import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerStatus;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository extends JpaRepository<HubDeliveryManager, UUID> {
    Optional<HubDeliveryManager> findByStatusOrderBySequence(DeliveryStatus deliveryStatus);
    Optional<CompanyDeliveryManger> findFirstByHubIdAndStatusAOrderBySequence(UUID hubId, DeliveryStatus status);

    long countByStatus(DeliveryManagerStatus status);
}
