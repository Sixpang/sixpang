package org.sixpang.notificationservice.application.service;

import org.sixpang.notificationservice.application.dto.NotificationRequestDto;

public interface NotificationService {
    void sendNotification(NotificationRequestDto request, String senderId);
}
