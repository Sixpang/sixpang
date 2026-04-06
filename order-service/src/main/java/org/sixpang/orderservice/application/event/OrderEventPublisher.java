package org.sixpang.orderservice.application.event;

public interface OrderEventPublisher {
    void publish(OrderCreatedEvent event);
}
