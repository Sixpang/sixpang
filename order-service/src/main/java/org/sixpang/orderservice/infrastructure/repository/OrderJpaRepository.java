package org.sixpang.orderservice.infrastructure.repository;

import org.sixpang.orderservice.domain.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
     Optional<Order> findByIdAndDeletedAtIsNull(UUID id);
     Page<Order> findAllByDeletedAtIsNull(Pageable pageable);
     Page<Order> findBySupplierIdAndDeletedAtIsNull(UUID supplierId, Pageable pageable);
     Page<Order> findByReceiverIdAndDeletedAtIsNull(UUID receiverId, Pageable pageable);
}
