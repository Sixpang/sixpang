package org.sixpang.notificationservice.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.sixpang.notificationservice.domain.model.entity.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class SlackAdapter {

    private final RestTemplate restTemplate;

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    public void send(Notification notification) {
        // 슬랙 Incoming Webhook 페이로드 생성
        Map<String, String> payload = Map.of("text", notification.getContent());

        try {
            restTemplate.postForEntity(slackWebhookUrl, payload, String.class);
        } catch (Exception e) {
            // 실제 서비스에서는 로그를 남기거나 재시도 로직을 추가합니다.
            System.err.println("Slack 전송 실패: " + e.getMessage());
        }
    }
}
