package org.sixpang.orderservice.application.event;

import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        UUID supplierId,
        UUID receiverId
) {
}
