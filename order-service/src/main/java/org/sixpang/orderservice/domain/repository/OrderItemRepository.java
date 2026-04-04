package org.sixpang.orderservice.domain.repository;

import org.sixpang.orderservice.domain.model.entity.OrderItem;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
    List<OrderItem> findByOrderIdAndDeletedAtIsNull(UUID orderId);
}
