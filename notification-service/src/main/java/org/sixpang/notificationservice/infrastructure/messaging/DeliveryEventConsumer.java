package org.sixpang.notificationservice.infrastructure.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventConsumer {
    // 추후 배송 쪽 구독하고 로직 구현
//    private final NotificationService notificationService;
//
//    @KafkaListener(topics = "delivery-topic", groupId = "notification-group")
//    public void consume(DeliveryEvent event) {
//        // 배송 상태가 '배송 시작(START_DELIVERY)'일 때만 슬랙 발송
//        if ("START_DELIVERY".equals(event.getStatus())) {
//            NotificationRequestDto request = NotificationRequestDto.builder()
//                    .receiverId(event.getSlackChannelId()) // 배송 업체나 담당자의 슬랙 ID
//                    .content(String.format("🚚 배송이 시작되었습니다! [주문번호: %s]", event.getOrderId()))
//                    .type(NotificationType.SLACK)
//                    .build();
//
//            // 시스템 자동 발송이므로 senderId는 "SYSTEM"
//            notificationService.sendNotification(request, "SYSTEM");
//        }
//    }
}
