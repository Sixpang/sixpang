package org.sixpang.orderservice.infrastructure.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductStockResponse {
    private UUID productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer stock; // 재고 수량
}
