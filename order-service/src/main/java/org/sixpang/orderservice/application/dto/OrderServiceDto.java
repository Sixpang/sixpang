package org.sixpang.orderservice.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderServiceDto {

    // 주문 생성 dto
    @Getter
    @Builder
    public static class Create {

        @NotNull
        private final UUID supplierId;

        @NotNull
        private final UUID receiverId;

        @NotNull
        private final Timestamp deadlineAt;

        @NotNull
        private final List<OrderItemCreate> orderItems;
    }

    // 주문 아이템 생성 dto
    @Getter
    @Builder
    public static class OrderItemCreate {

        @NotNull
        private final UUID productId;

        @NotNull
        private final Integer count;
    }

    // 주문 수정 dto
    @Getter
    @Builder
    public static class Update {
        private final Timestamp deadlineAt;
    }
}
