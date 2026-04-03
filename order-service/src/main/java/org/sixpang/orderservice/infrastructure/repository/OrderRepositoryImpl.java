package org.sixpang.orderservice.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderRepository orderRepository;
    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findByIdAndDeletedAtIsNull(UUID id) {
        return orderJpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Page<Order> findAllByDeletedAtIsNull(Pageable pageable) {
        return orderJpaRepository.findAllByDeletedAtIsNull(pageable);
    }

    @Override
    public Page<Order> findBySupplierIdAndDeletedAtIsNull(UUID supplierId, Pageable pageable) {
        return orderJpaRepository.findBySupplierIdAndDeletedAtIsNull(supplierId, pageable);
    }

    public Page<Order> findByReceiverIdAndDeletedAtIsNull(UUID receiverId, Pageable pageable) {
        return orderJpaRepository.findByReceiverIdAndDeletedAtIsNull(receiverId, pageable);
    }
}
