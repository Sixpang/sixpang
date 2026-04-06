package org.sixpang.orderservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderResponse {

    private UUID id;
    private UUID supplierId;
    private UUID receiverId;
    private OrderStatus orderStatus;
    private BigDecimal totalPrice;
    private Timestamp deadlineAt;


    // Entity -> DTO 반환
    public static OrderResponse fromEntity(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getSupplierId(),
                order.getReceiverId(),
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getDeadlineAt()
        );
    }

}
