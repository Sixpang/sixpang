package org.sixpang.productservice.application.dto;


import org.sixpang.productservice.domain.model.Inventory;

import java.util.UUID;


public record InventoryResponse(

        UUID productId,
        Long quantity
) {
    public static InventoryResponse from(Inventory inventory){
        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getQuantity()
        );
    }
}
