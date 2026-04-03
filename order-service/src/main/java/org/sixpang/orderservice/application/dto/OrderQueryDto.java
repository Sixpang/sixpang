package org.sixpang.orderservice.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderQueryDto {

    // 목록 조회
    @Getter
    @AllArgsConstructor
    public static class OrderInfo {
        private UUID id;
    }
}
