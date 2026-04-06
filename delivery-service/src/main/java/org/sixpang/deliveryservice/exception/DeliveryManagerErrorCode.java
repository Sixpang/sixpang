package org.sixpang.deliveryservice.exception;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DeliveryManagerErrorCode implements ErrorCode {
    // [404 NOT_FOUND] 조회 실패
    MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 배송 담당자를 찾을 수 없습니다."),

    // [400 BAD_REQUEST] 상태 및 비즈니스 로직 제약
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "현재 상태에서는 변경이 불가능합니다."),
    MANAGER_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "배송 담당자 정원이 초과되었습니다."),
    HUB_ID_REQUIRED(HttpStatus.BAD_REQUEST, "업체 배송 담당자는 허브 ID가 필수입니다."),
    MANAGER_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "현재 배송 가능한 담당자가 아닙니다."),
    UNSUPPORTED_TYPE(HttpStatus.BAD_REQUEST, "담당자 타입이 지정되지 않았습니다."),

    // [409 CONFLICT] 중복 및 데이터 충돌
    ALREADY_EXISTS_MANAGER(HttpStatus.CONFLICT, "이미 배송 담당자로 등록된 사용자입니다.");

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
