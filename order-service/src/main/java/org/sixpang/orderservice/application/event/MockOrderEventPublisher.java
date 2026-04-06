package org.sixpang.orderservice.application.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary // 이건 Mock데이터인데 kafka쪽에 중복적으로 있어서 이 어노테이션으로 스프링이 감지하게 함
public class MockOrderEventPublisher implements OrderEventPublisher {
    @Override
    public void publish(OrderCreatedEvent event) {
        log.info("📢 [Mock Kafka] 이벤트 발행: {}", event);
    }
}
