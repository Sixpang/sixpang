package org.sixpang.orderservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.model.entity.OrderItem;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderDetailResponse {

    private UUID id;
    private UUID supplierId;
    private UUID receiverId;
    private OrderStatus orderStatus;
    private BigDecimal totalPrice;
    private Timestamp deadlineAt;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> orderItems;

    public static OrderDetailResponse fromEntity(Order order, List<OrderItem> orderItems) {
        return new OrderDetailResponse(
                order.getId(),
                order.getSupplierId(),
                order.getReceiverId(),
                order.getOrderstatus(),
                order.getTotalPrice(),
                order.getDeadlineAt(),
                order.getCreatedAt(),
                orderItems.stream()
                        .map(OrderItemResponse::fromEntity)
                        .toList()
        );
    }
}
