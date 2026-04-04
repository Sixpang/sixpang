package org.sixpang.orderservice.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class OrderRequestDto {

    // 주문 생성 요청
    @Getter
    @NoArgsConstructor
    public static class CreateOrderRequest {

        @NotNull(message = "공급 업체 ID는 필수 입니다.")
        private UUID supplierId;

        @NotNull(message = "수령 업체 ID는 필수입니다.")
        private UUID receiverId;

        @NotNull(message = "납입 기한은 필수입니다.")
        private Timestamp deadlineAt;

        @NotNull(message = "주문 아이템은 필수입니다.")
        private List<OrderItemRequest> orderItems;
    }

    // 주문 아이템 요청
    @Getter
    @NoArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "상품 ID는 필수입니다.")
        private UUID productId;

        @NotNull(message = "수량은 필수입니다.")
        private Integer count;
    }

    // 주문 수정 요청
    @Getter
    @NoArgsConstructor
    public static class UpdateOrderRequest {
        private Timestamp deadlineAt;
    }
}
