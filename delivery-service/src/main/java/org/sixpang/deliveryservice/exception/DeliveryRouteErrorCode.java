package org.sixpang.deliveryservice.exception;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DeliveryRouteErrorCode implements ErrorCode {
    // [400 BAD_REQUEST] 상태 및 비즈니스 로직 제약
    DELIVERY_PATH_ERROR(HttpStatus.BAD_REQUEST, "배송 경로 생성 실패로 인해 주문이 취소됩니다."),
    INVALID_ROUTE_SEQUENCE(HttpStatus.BAD_REQUEST, "배송 순서는 0 이상의 숫자여야 합니다."),
    INVALID_ROUTE_DISTANCE(HttpStatus.BAD_REQUEST, "예상 거리는 음수일 수 없습니다."),
    INVALID_ROUTE_TIME(HttpStatus.BAD_REQUEST, "예상 시간은 음수일 수 없습니다."),
    ROUTE_NOT_FOUND(HttpStatus.BAD_REQUEST, "경로를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
