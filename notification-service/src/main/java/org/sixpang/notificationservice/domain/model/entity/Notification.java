package org.sixpang.notificationservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.commonserver.entity.BaseEntity;
import org.sixpang.notificationservice.domain.model.enums.NotificationType;

import java.util.UUID;

@Entity
@Getter
@Table(name = "p_notification", schema = "notification_service")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String receiverId; // 슬랙 채널 ID 등

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "sender_id")
    private String senderId;

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(String receiverId, String content, NotificationType type, String senderId) {
        this.receiverId = receiverId;
        this.content = content;
        this.type = type;
        this.senderId = senderId;
    }

    public static Notification create(String receiverId, String content, NotificationType type, String senderId) {
        return Notification.builder()
                .receiverId(receiverId)
                .content(content)
                .type(type)
                .senderId(senderId)
                .build();
    }
}
