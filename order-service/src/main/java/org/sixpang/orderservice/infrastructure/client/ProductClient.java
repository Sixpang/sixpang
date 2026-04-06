package org.sixpang.orderservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    // 상품 재고 확인
    @GetMapping("/api/products/{productId}/stock")
    ProductStockRespose getProductStock(@PathVariable UUID productId);
}
