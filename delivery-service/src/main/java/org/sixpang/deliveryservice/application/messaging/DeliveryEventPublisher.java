package org.sixpang.deliveryservice.application.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sixpang.deliveryservice.application.dto.DeliveryCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.delivery-created}")  // ⭐ 수정됨
    private String deliveryCreatedTopic;

    public void publishDeliveryCreated(DeliveryCreatedEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(deliveryCreatedTopic, event.getDeliveryId().toString(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("배송 생성 이벤트 발행 성공 - DeliveryId: {}, Topic: {}, Partition: {}, Offset: {}",
                            event.getDeliveryId(),
                            result.getRecordMetadata().topic(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("배송 생성 이벤트 발행 실패 - DeliveryId: {}, Error: {}",
                            event.getDeliveryId(), ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("배송 생성 이벤트 발행 중 예외 발생 - DeliveryId: {}", event.getDeliveryId(), e);
        }
    }
}