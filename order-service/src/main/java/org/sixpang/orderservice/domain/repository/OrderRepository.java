package org.sixpang.orderservice.domain.repository;

import org.sixpang.orderservice.domain.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);
    Optional<Order> findByIdAndDeletedAtIsNull(UUID id);
    Page<Order> findAllByDeletedAtIsNull(Pageable pageable);
    Page<Order> findBySupplierIdAndDeletedAtIsNull(UUID supplierId, Pageable pageable);
    Page<Order> findByReceiverIdAndDeletedAtIsNull(UUID receiverId, Pageable pageable);

}
