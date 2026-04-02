package org.sixpang.commonserver.response;

import lombok.Getter;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final int status;
    private final String errorCode;
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorResponse(ErrorCode errorCode, String message){
        this.status = errorCode.getStatus().value();
        this.errorCode = errorCode.getStatus().name();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(HttpStatus httpStatus, String message){
        this.status = httpStatus.value();
        this.errorCode = httpStatus.getReasonPhrase();
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
