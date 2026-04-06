package org.sixpang.orderservice.application.service;

import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.orderservice.presentation.dto.CreateOrderRequest;
import org.sixpang.orderservice.presentation.dto.OrderDetailResponse;
import org.sixpang.orderservice.presentation.dto.OrderResponse;
import org.sixpang.orderservice.presentation.dto.UpdateOrderRequest;

import java.util.UUID;


public interface OrderService {

    OrderDetailResponse createOrder(CreateOrderRequest request);

    OrderDetailResponse getOrder(UUID orderId);

    PageResponse<OrderResponse> getOrders(int page, int size, String sortBy, String sortDir);

    PageResponse<OrderResponse> getOrdersBySupplierId(UUID supplierId, int page, int size, String sortBy, String sortDir);

    PageResponse<OrderResponse> getOrdersByReceiverId(UUID receiverId, int page, int size, String sortBy, String sortDir);

    OrderDetailResponse updateOrder(UUID orderId, UpdateOrderRequest request);

    void deleteOrder(UUID orderId, UUID deletedBy);
}
