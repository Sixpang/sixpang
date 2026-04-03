package org.sixpang.orderservice.application.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sixpang.orderservice.domain.model.enums.DeliveryStatus;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderQueryDto {

    // 목록 조회
    @Getter
    @AllArgsConstructor
    public static class OrderInfo {

        private UUID supplierId;       // 공급 업체 ID 필터
        private UUID receiverId;       // 수령 업체 ID 필터

        @Enumerated(EnumType.STRING)
        private OrderStatus orderStatus;       // 주문 상태 필터

        @Enumerated(EnumType.STRING)
        private DeliveryStatus deliveryStatus; // 배송 상태 필터

        private Timestamp deadlineFrom;  // 납입 기한 시작
        private Timestamp deadlineTo;    // 납입 기한 끝

        private BigDecimal minTotalPrice; // 최소 주문 금액
        private BigDecimal maxTotalPrice; // 최대 주문 금액

        // 페이징
        private int page = 0;
        private int size = 10;

        // 정렬
        private String sortBy = "createdAt";  // 정렬 기준 컬럼
        private String sortDir = "desc";      // asc / desc
    }

}
