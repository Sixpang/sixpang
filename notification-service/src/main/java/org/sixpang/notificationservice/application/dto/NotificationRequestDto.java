package org.sixpang.notificationservice.application.dto;

import lombok.Builder;
import lombok.Getter;
import org.sixpang.notificationservice.domain.model.enums.NotificationType;

@Getter
@Builder // 이벤트를 DTO로 변환하기 쉽게 추가
public class NotificationRequestDto {
    private String receiverId;
    private String content;
    private NotificationType type;
}
