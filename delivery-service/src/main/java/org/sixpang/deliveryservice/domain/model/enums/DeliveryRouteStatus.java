package org.sixpang.deliveryservice.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryRouteStatus {
    HUB_WAITING("해당 구간 시작 전"),
    HUB_TO_HUB("현재 허브에서 다음 허브로 출발"),
    WAY_ARRIVAL("다음 허브에 도착"),
    ARRIVAL("수령지 허브 도착"),
    PICKUP("업체 배송 담당자 픽업"),
    IN_DELIVERY("업체 배송중"),
    DONE("배송완료");

    private final String description;
}