package org.sixpang.orderservice.infrastructure.repository;

import org.sixpang.orderservice.domain.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    // Optional<Order> findByAndDeletedAtIsNull(UUID id);
}
