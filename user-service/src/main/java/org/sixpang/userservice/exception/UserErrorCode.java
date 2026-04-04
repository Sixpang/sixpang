package org.sixpang.userservice.exception;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EXISTS_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    EXISTS_PHONE(HttpStatus.CONFLICT, "이미 존재하는 전화번호입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "잘못된 상태 값입니다."),

    ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "이미 승인된 상태입니다."),
    ALREADY_REJECTED(HttpStatus.BAD_REQUEST, "이미 거절된 상태입니다."),
    CANNOT_APPROVE(HttpStatus.BAD_REQUEST, "승인할 수 없는 상태입니다."),
    CANNOT_REJECT(HttpStatus.BAD_REQUEST, "거절할 수 없는 상태입니다."),

    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");

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
