package org.sixpang.authservice.exception;

import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;


public enum AuthErrorCode implements ErrorCode {

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    NOT_APPROVED(HttpStatus.FORBIDDEN, "승인되지 않은 사용자입니다."),
    REJECTED_USER(HttpStatus.FORBIDDEN, "거절된 사용자입니다.");

    private final HttpStatus status;
    private final String message;

    AuthErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}