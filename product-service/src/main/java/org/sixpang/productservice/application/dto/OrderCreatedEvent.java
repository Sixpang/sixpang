package org.sixpang.productservice.application.dto;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID supplierId,
        UUID receiverId
) {
}
