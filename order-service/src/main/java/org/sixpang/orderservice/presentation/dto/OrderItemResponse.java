package org.sixpang.orderservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sixpang.orderservice.domain.model.entity.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderItemResponse {

    private UUID id;
    private UUID productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer count;

    public static OrderItemResponse fromEntity(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductPrice(),
                item.getCount()
        );
    }

}
