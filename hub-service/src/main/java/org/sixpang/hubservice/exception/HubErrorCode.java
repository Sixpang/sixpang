package org.sixpang.hubservice.exception;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum HubErrorCode implements ErrorCode {
    HUB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 허브입니다."),
    EXISTS_HUB(HttpStatus.BAD_REQUEST, "이미 존재하는 허브입니다."),
    HUB_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 허브입니다.");

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
