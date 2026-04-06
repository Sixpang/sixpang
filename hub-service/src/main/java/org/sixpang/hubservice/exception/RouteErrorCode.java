package org.sixpang.hubservice.exception;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum RouteErrorCode implements ErrorCode {
    AVAILABLE_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 허브에서 갈 수 있는 경로가 없습니다."),
    DIRECT_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 허브 간 이동 경로를 찾을 수 없습니다."),
    OPTIMAL_ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 허브 간 최적 이동 경로를 찾을 수 없습니다.");

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
