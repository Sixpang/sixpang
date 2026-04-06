package org.sixpang.commonserver.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final int status;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    public ApiResponse(HttpStatus status, String message, T data){
        this.status = status.value();
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> of(HttpStatus status, String message, T data){
        return new ApiResponse<>(status, message, data);
    }

    public static <T> ApiResponse<T> of(String message, T data){
        return new ApiResponse<>(HttpStatus.OK, message, data);
    }
}
