package org.sixpang.deliveryservice.application.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sixpang.deliveryservice.application.dto.OrderCreatedEvent;
import org.sixpang.deliveryservice.application.service.service.DeliveryCreationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventConsumer {
    private final DeliveryCreationService deliveryService;

    @KafkaListener(
            topics = "order-created-topic",
            groupId = "delivery-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("[Delivery Service] 배송 생성 이벤트 수신: orderId={}", event.orderId());

        try {
            // 멱등성 체크 및 배송 생성 로직 호출
            deliveryService.createDeliveryFromOrder(event);
        } catch (Exception e) {
            log.error("배송 생성 중 오류 발생: orderId={}, error={}", event.orderId(), e.getMessage());
            // 이후 AI 알림 및 에러 핸들링 로직 추가 지점
        }
    }
}
