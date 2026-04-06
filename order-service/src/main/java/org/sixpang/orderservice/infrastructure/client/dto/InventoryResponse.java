package org.sixpang.orderservice.infrastructure.client.dto;

import java.util.UUID;

public record InventoryResponse(
        UUID productId,
        Long quantity
) {
}
