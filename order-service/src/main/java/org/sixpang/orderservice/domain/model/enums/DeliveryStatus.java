package org.sixpang.orderservice.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    PENDING("배송 예정"),
    DELIVERING("배송 중"),
    COMPLETDELIVERY("배송 완료");
    // 배송 취소?

    private final String description;
}
