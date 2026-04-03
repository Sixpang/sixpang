package org.sixpang.orderservice.application.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.orderservice.domain.model.enums.DeliveryStatus;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderQueryDto {

    // 목록 조회
    @Getter
    @AllArgsConstructor
    public static class OrderInfo {

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
    public static class OrderDetail {
        private UUID id;
        private UUID supplierId;
        private UUID receiverId;
        private OrderStatus orderStatus;
        private DeliveryStatus deliveryStatus;
        private BigDecimal totalPrice;
        private Timestamp deadlineAt;
        private LocalDateTime createdAt;
        private List<OrderItemInfo> orderItems;
    }

    // 주문 아이템 조회
    @Getter
    @AllArgsConstructor
    public static class OrderItemInfo {
        private UUID id;
        private UUID productId;
        private String productName;
        private BigDecimal ProductPrice;
        private Integer count;
    }

}
