package org.sixpang.hubservice.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HubStatus {
    ACTIVE("운영 중"),
    SUSPENDED("일시 중단"),
    CLOSED("운영 종료");

    private final String description;
}
