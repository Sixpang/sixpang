package org.sixpang.orderservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.model.entity.OrderItem;
import org.sixpang.orderservice.domain.model.enums.DeliveryStatus;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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

        public static OrderResponse from(Order order) {
            return new OrderResponse(
                    order.getId(),
                    order.getSupplierId(),
                    order.getReceiverId(),
                    order.getOrderstatus(),
                    order.getDeliveryStatus(),
                    order.getTotalPrice(),
                    order.getDeadlineAt()
            );
        }
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
        private LocalDateTime createdAt;
        private List<OrderItemResponse> orderItems;

        public static OrderDetailResponse from(
                Order order, List<OrderItem> orderItems
        ) {
            return new OrderDetailResponse(
                    order.getId(),
                    order.getSupplierId(),
                    order.getReceiverId(),
                    order.getOrderstatus(),
                    order.getDeliveryStatus(),
                    order.getTotalPrice(),
                    order.getDeadlineAt(),
                    order.getCreatedAt(),
                    orderItems.stream()
                            .map(OrderItemResponse::from)
                            .toList()
            );
        }
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

        public static OrderItemResponse from(OrderItem item) {
            return new OrderItemResponse(
                    item.getId(),
                    item.getProductId(),
                    item.getProductName(),
                    item.getProductPrice(),
                    item.getCount()
            );
        }
    }
}
