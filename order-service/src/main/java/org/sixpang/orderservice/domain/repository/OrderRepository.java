package org.sixpang.orderservice.domain.repository;

import org.sixpang.orderservice.domain.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    // 사용자별 주문 조회
    Page<Order> findByUserId(UUID userId, Pageable pageable);

    // 주문 전체 조회 (MASTER)
    //Page<Order> order = orderRepository.findAll(pageRequest.of(0, 10);

    // 주문 ID로 조회
    //Optional<Order> findById(UUID id);

    // 주문 상세 조회
    //Optional<Order> findByAndUserID(UUID id, UUID userId);

}
