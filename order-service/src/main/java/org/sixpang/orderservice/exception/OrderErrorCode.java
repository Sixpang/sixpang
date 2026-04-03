package org.sixpang.orderservice.exception;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode{

    ALREADY_CANCELLED(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.");

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
