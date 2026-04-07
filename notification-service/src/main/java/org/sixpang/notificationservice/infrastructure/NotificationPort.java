package org.sixpang.notificationservice.infrastructure;

import org.sixpang.notificationservice.domain.model.entity.Notification;
import org.sixpang.notificationservice.domain.model.enums.NotificationType;

public interface NotificationPort {
    void send(Notification notification);

    boolean supports(NotificationType type);
}
