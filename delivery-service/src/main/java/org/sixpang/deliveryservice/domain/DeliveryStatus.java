package org.sixpang.deliveryservice.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    HUB_WAITING("출발 허브 대기중"),
    HUB_TO_HUB("허브간 이동중"),
    WAY_ARRIVAL("경유지 허브 도착"),
    ARRIVAL("수령지 허브 도착"),
    PICKUP("업체 배송 담당자 픽업"),
    IN_DELIVERY("업체 배송중"),
    DONE("배송완료");

    private final String description;
}
