package org.sixpang.orderservice.infrastructure.kafka.clients.consumer;

import lombok.extern.slf4j.Slf4j;
import org.sixpang.orderservice.application.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumerService {

    @KafkaListener(topics = "order-created-topic", groupId = "order-group")
    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("🎉 Kafka 메시지 수신: {}", event);
    }
    
}
