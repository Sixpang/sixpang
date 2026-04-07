package org.sixpang.productservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sixpang.productservice.application.dto.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    /*@KafkaListener(
            topics = "order-created-topic",
            groupId = "product-service-group"
            //containerFactory = "kafkaListenerContainerFactory"
    )

     */


    public void consumeOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("[Product Service] 주문 생성 이벤트 수신 - orderId={}", event.orderId());

        try {
            // 여기서 재고 차감, 검증 같은 실제 로직 연결
            // ex)
            // productService.confirmStockReduction(event.orderId());

            log.info("[Product Service] 이벤트 처리 완료 - orderId={}", event.orderId());
        } catch (Exception e) {
            log.error("[Product Service] 이벤트 처리 중 오류 발생 - orderId={}, message={}",
                    event.orderId(), e.getMessage(), e);
        }
    }
}
