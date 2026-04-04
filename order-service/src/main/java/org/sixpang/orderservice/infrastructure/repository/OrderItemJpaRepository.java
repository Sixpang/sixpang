package org.sixpang.orderservice.infrastructure.repository;

import org.sixpang.orderservice.domain.model.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemJpaRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderIdAndDeletedAtIsNull(UUID orderId);
}
