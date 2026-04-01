package org.sixpang.orderservice.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    CONFIRMED("승인"),
    CANCELLED("취소");

    private final String description;
}
