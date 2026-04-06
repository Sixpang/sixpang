package org.sixpang.orderservice.application.service;

import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.orderservice.presentation.dto.CreateOrderRequest;
import org.sixpang.orderservice.presentation.dto.OrderDetailResponse;
import org.sixpang.orderservice.presentation.dto.OrderResponse;
import org.sixpang.orderservice.presentation.dto.UpdateOrderRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface OrderService {

    // 주문 생성, 상세 조회
    OrderDetailResponse createOrder(CreateOrderRequest request);

    OrderDetailResponse getOrder(UUID orderId, UserPrincipal user);

    // 페이징 조회의 파라미터를 Pageable 하나로 통일
    PageResponse<OrderResponse> getOrders(Pageable pageable);

    PageResponse<OrderResponse> getOrdersBySupplierId(UUID supplierId, Pageable pageable);

    PageResponse<OrderResponse> getOrdersByReceiverId(UUID receiverId, Pageable pageable);

    // 수정, 삭제
    OrderDetailResponse updateOrder(UUID orderId, UpdateOrderRequest request, UserPrincipal user);

    void deleteOrder(UUID orderId, UserPrincipal user);
}

