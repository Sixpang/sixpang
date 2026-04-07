package org.sixpang.deliveryservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryRouteRepositoryImpl implements DeliveryRouteRepository {

    private final DeliveryRouteJpaRepository jpaRepository;

    @Override
    public DeliveryRoute save(DeliveryRoute deliveryRoute) {
        return jpaRepository.save(deliveryRoute);
    }

    @Override
    public Optional<DeliveryRoute> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<DeliveryRoute> findAllByDelivery(Delivery delivery) {
        return jpaRepository.findAllByDeliveryIdOrderByHubSequenceAsc(delivery);
    }
}