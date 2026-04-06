package org.sixpang.orderservice.application.service;

import org.sixpang.orderservice.presentation.dto.CreateOrderRequest;
import org.sixpang.orderservice.presentation.dto.OrderDetailResponse;
import org.sixpang.orderservice.presentation.dto.OrderPageResponse;
import org.sixpang.orderservice.presentation.dto.UpdateOrderRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


public interface OrderService {

    OrderDetailResponse createOrder(CreateOrderRequest request);

    OrderDetailResponse getOrder(UUID orderId);

    OrderPageResponse getOrders(Pageable pageable);

    // Page 대신 OrderPageResponse로 감싸서 반환
    OrderPageResponse getOrdersBySupplierId(UUID supplierId, Pageable pageable);

    OrderPageResponse getOrdersByReceiverId(UUID receiverId, Pageable pageable);

    OrderDetailResponse updateOrder(UUID orderId, UpdateOrderRequest request);

    void deleteOrder(UUID orderId, UUID deletedBy);
}
