package org.sixpang.commonserver.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.sixpang.commonserver.response.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final View error;

    public GlobalExceptionHandler(View error){ this.error = error; }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handlerCustomException(CustomException e){
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode, e.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorResponse);
    }
}
