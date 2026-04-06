package org.sixpang.deliveryservice.exception;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DeliveryErrorCode implements ErrorCode {

    // [401 UNAUTHORIZED] 인증 관련
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),

    // [403 FORBIDDEN] 권한 관련
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 배송건에 대한 권한이 없습니다."),

    // [404 NOT_FOUND] 조회 실패
    DELIVERY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 배송 정보를 찾을 수 없습니다."),
    MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 배송 담당자를 찾을 수 없습니다."),
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "출발 또는 목적지 허브 ID가 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),

    // [400 BAD_REQUEST] 상태 및 비즈니스 로직 제약
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "현재 상태에서는 변경이 불가능합니다."),
    DELIVERY_PATH_ERROR(HttpStatus.BAD_REQUEST, "배송 경로 생성 실패로 인해 주문이 취소됩니다."),
    MANAGER_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "배송 담당자 정원이 초과되었습니다."),
    HUB_ID_REQUIRED(HttpStatus.BAD_REQUEST, "업체 배송 담당자는 허브 ID가 필수입니다."),
    MANAGER_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "현재 배송 가능한 담당자가 아닙니다."),
    UNSUPPORTED_TYPE(HttpStatus.BAD_REQUEST, "담당자 타입이 지정되지 않았습니다."),

    // [409 CONFLICT] 중복 및 데이터 충돌
    ALREADY_EXISTS_MANAGER(HttpStatus.CONFLICT, "이미 배송 담당자로 등록된 사용자입니다."),
    ALREADY_EXISTS_DELIVERY(HttpStatus.CONFLICT, "이미 존재하는 배송건입니다.");

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
