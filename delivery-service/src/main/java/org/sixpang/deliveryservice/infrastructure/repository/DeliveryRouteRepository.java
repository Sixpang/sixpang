package org.sixpang.deliveryservice.infrastructure.repository;

import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRouteRepository {
    DeliveryRoute save(DeliveryRoute deliveryRoute);

    Optional<DeliveryRoute> findById(UUID id);

    List<DeliveryRoute> findAllByDelivery(Delivery delivery);

    List<DeliveryRoute> findByDeliveryIdOrderByHubSequenceAsc(UUID deliveryId);

}
