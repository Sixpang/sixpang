package org.sixpang.deliveryservice.domain.repository;

import org.sixpang.deliveryservice.domain.model.entity.HubDeliveryManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryManagerRepository extends JpaRepository<HubDeliveryManager, UUID> {
    Optional<HubDeliveryManager> findFirstByHubIdAndStatusOrderByDeliverySequenceAsc(UUID deliveryManagerId);

}
