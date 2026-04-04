package org.sixpang.orderservice.application.service;

import org.sixpang.orderservice.presentation.dto.OrderRequestDto;
import org.sixpang.orderservice.presentation.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    // 주문 생성
    OrderResponseDto.OrderDetailResponse createOrder(OrderRequestDto.CreateOrderRequest request);

    // 주문 단건 조회
    OrderResponseDto.OrderDetailResponse getOrder(UUID orderID);

    // 주문 전체 목록 조회 (MASTER)
    Page<OrderResponseDto.OrderResponse> getOrders(Pageable pageable);

    // 공급 업체별 주문 목록 조회
    Page<OrderResponseDto.OrderResponse> getOrdersBySupplierId(UUID supplierId, Pageable pageable);

    // 수령 업체별 주문 목록 조회
    Page<OrderResponseDto.OrderResponse> getOrdersByreceiverId(UUID receiverId, Pageable pageable);

    // 주문 수정
    OrderResponseDto.OrderDetailResponse updateOrder(UUID orderId, OrderRequestDto.UpdateOrderRequest request);

    // 주문 삭제 (softDelete)
    void deleteOrder(UUID orderID, UUID deletedBy);

}
