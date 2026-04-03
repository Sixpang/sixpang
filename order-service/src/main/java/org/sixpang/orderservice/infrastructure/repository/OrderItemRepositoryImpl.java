package org.sixpang.orderservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.orderservice.domain.model.entity.OrderItem;
import org.sixpang.orderservice.domain.repository.OrderItemRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final OrderItemJpaRepository orderItemJpaRepository;

    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemJpaRepository.save(orderItem);
    }

    @Override
    public List<OrderItem> findByOrderIdAndDeletedAtIsNull(UUID orderID) {
        return orderItemJpaRepository.findByOrderIdAndDeletedAtIsNull(orderID);
    }
}
