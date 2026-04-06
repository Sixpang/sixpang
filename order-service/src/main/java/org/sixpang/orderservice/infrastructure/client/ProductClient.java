package org.sixpang.orderservice.infrastructure.client;

import org.sixpang.orderservice.infrastructure.client.dto.InventoryResponse;
import org.sixpang.orderservice.infrastructure.client.dto.UpdateInventoryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {

    // 재고 조회
    @GetMapping("/api/products/{productId}/inventory")
    InventoryResponse getInventory(@PathVariable UUID productId);

    // 재고 감소
    @PatchMapping("/api/products/{productId}/inventory/decrease")
    InventoryResponse decreaseInventory(
            @PathVariable UUID productId,
            @RequestBody UpdateInventoryRequest request
    );
}
