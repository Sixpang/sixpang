package org.sixpang.notificationservice.domain.repository;

import org.sixpang.notificationservice.domain.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}
