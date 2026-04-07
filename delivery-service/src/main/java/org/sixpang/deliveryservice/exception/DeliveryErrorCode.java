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
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "출발 또는 목적지 허브 ID가 존재하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    MANAGER_ASSIGNMENT_FAILED(HttpStatus.BAD_REQUEST, "담당자 배정에 실패하였습니다."),

    // [400 BAD_REQUEST] 상태 및 비즈니스 로직 제약
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "현재 상태에서는 변경이 불가능합니다."),

    // [409 CONFLICT] 중복 및 데이터 충돌
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
