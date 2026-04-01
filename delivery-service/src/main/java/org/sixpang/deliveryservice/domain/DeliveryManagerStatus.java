package org.sixpang.deliveryservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryManagerStatus {
    ON_TASK("배송중"),
    WAIT("대기중"),
    OFF("휴무"),
    INACTIVE("근무 불가자");

    private final String description;
}
