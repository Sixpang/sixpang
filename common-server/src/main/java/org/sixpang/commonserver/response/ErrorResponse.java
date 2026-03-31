package org.sixpang.commonserver.response;

import lombok.Getter;
import org.sixpang.commonserver.global.ErrorCode;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final boolean success;
    private final String message;
    private final LocalDateTime timestamp;

    public ErrorResponse(String message){
        this.success = false;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static ErrorResponse of(ErrorCode errorCode){
        return new ErrorResponse(errorCode.getMessage());
    }
}
