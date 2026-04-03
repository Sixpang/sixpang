package org.sixpang.userservice.exception;

import org.sixpang.commonserver.global.GlobalErrorCode;
import org.sixpang.commonserver.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class UserExceptionHandler {

    // Validation 실패 시 발생하는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e
    ){
        String message = e.getBindingResult()
                .getFieldErrors()
                .get(0)
                .getDefaultMessage();

        ErrorResponse errorResponse = new ErrorResponse(
                GlobalErrorCode.VALIDATION_ERROR,
                message
        );

        return ResponseEntity
                .status(GlobalErrorCode.VALIDATION_ERROR.getStatus())
                .body(errorResponse);
    }
}