package org.sixpang.notificationservice.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    SLACK("슬랙"),
    EMAIL("이메일");

    private final String description;
}
