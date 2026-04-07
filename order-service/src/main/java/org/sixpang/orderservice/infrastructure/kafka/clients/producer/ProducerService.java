package org.sixpang.orderservice.infrastructure.kafka.clients.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProducerService {

    // ⚠️ String에서 Object로 타입을 맞춰야 Config와 연결됩니다.
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, String key, Object message) {
        // 테스트용 루프
        for (int i = 0; i < 10; i++) {
            // message가 String이면 문자열 뒤에 숫자가 붙고,
            // 객체라면 그대로 직렬화되어 나갑니다.
            kafkaTemplate.send(topic, key, message.toString() + " " + i);
        }
    }
}