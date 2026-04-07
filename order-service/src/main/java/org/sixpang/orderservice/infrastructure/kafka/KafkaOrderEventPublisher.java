package org.sixpang.orderservice.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.sixpang.orderservice.application.event.OrderCreatedEvent;
import org.sixpang.orderservice.application.event.OrderEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(OrderCreatedEvent event) {
        // record 필드 접근은 event.orderId() 등으로
        kafkaTemplate.send("order-created-topic", event.orderId().toString(), event);
    }
}
