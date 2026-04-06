package org.sixpang.notificationservice.domain.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_slack_messages")
public class SlackMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String receiverId; // 담당자 아이디 혹은 채널명

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime sentAt;

    @Builder
    public SlackMessage(String receiverId, String message) {
        this.receiverId = receiverId;
        this.message = message;
        this.sentAt = LocalDateTime.now();
    }
}
