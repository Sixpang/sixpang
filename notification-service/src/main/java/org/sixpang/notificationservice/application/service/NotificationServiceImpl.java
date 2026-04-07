package org.sixpang.notificationservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.notificationservice.application.dto.NotificationRequestDto;
import org.sixpang.notificationservice.domain.model.entity.Notification;
import org.sixpang.notificationservice.domain.model.enums.NotificationType;
import org.sixpang.notificationservice.domain.repository.NotificationRepository;
import org.sixpang.notificationservice.infrastructure.adapter.SlackAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SlackAdapter slackAdapter;

    @Override
    @Transactional
    public void sendNotification(NotificationRequestDto request, String senderId) {
        // 1. 엔티티 생성 및 저장
        Notification notification = Notification.create(
                request.getReceiverId(),
                request.getContent(),
                request.getType(),
                senderId
        );
        notificationRepository.save(notification);

        // 2. 타입에 따른 발송 로직 분기 (확장 가능)
        if (notification.getType() == NotificationType.SLACK) {
            slackAdapter.send(notification);
        }
    }
}
