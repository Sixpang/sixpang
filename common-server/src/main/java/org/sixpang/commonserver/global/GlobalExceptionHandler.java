package org.sixpang.commonserver.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.sixpang.commonserver.response.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handlerCustomException(CustomException e){
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode, e.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(errorResponse);
    }

    // DTO 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ){
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        // 요청 필드 에러 메시지 리스트
        List errorMessages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        // 모든 메시지를 하나로 묶기
        String errorMessage = String.join(", ", errorMessages);

        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus,
                errorMessage
        );

        return ResponseEntity
                .status(httpStatus)
                .body(errorResponse);
    }

    // 그 외 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e){
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("[예외 발생] : ", e);

        ErrorResponse errorResponse = new ErrorResponse(
                httpStatus,
                e.getMessage()
        );

        return ResponseEntity
                .status(httpStatus)
                .body(errorResponse);
    }
}
