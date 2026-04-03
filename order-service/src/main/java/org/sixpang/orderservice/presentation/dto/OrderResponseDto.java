package org.sixpang.orderservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.orderservice.domain.model.enums.DeliveryStatus;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class OrderResponseDto {

    // 목록 조회
    @Getter
    @AllArgsConstructor
    public static class OrderResponse {
        private UUID id;
        private UUID supplierId;
        private UUID receiverId;
        private OrderStatus orderStatus;
        private DeliveryStatus deliveryStatus;
        private BigDecimal totalPrice;
        private Timestamp deadlineAt;
    }

    // 상세 조회
    @Getter
    @AllArgsConstructor
    public static class OrderDetailResponse {
        private UUID id;
        private UUID supplierId;
        private UUID receiverId;
        private OrderStatus orderStatus;
        private DeliveryStatus deliveryStatus;
        private BigDecimal totalPrice;
        private Timestamp deadlineAt;
        private List<OrderItemResponse> orderItems;
    }

    // 주문 아이템
    @Getter
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID id;
        private UUID productId;
        private String productName;
        private BigDecimal productPrice;
        private Integer count;
        private BigDecimal subTotal;
    }

    // 생성 / 수정
    @Getter
    @AllArgsConstructor
    public static class OrderSimpleResponse {
        private UUID id;
        private OrderStatus orderStatus;
        private DeliveryStatus deliveryStatus;
        private BigDecimal totalPrice;
    }
}
