package org.sixpang.deliveryservice.infrastructure.repository;

import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryRouteJpaRepository extends JpaRepository<DeliveryRoute, UUID> {
    List<DeliveryRoute> findAllByDeliveryIdOrderByHubSequenceAsc(Delivery delivery);
}
