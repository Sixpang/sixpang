package org.sixpang.orderservice.domain.repository;

import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.model.enums.DeliveryStatus;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>{

    // 주문 단건 조회 (삭제 제외)
    Optional<Order> findByIdAndDeletedAtIsNull(UUID id);

    // 공급 업체별 주문 조회
    Page<Order> findBySupplierId(UUID supplierId, Pageable pageable);

    // 수령 업체별 주문 조회
    Page<Order> findByReceiverId(UUID receiverId, Pageable pageable);

    // 주문 상태별 조회
    Page<Order> findByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    // 배송 상태별 조회
    Page<Order> findByDeliveryStatus(DeliveryStatus deliveryStatus, Pageable pageable);

    // 전체 주문 조회 (삭제 제외)
    Page<Order> findAllByDeletedAtIsNull(Pageable pageable);

}
