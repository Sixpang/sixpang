package org.sixpang.orderservice.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "공급 업체 ID는 필수 입니다.")
    private UUID supplierId;

    @NotNull(message = "수령 업체 ID는 필수입니다.")
    private UUID receiverId;

    @NotNull(message = "납입 기한은 필수입니다.")
    private Timestamp deadlineAt;

    @NotNull(message = "주문 아이템은 필수입니다.")
    private List<OrderItemRequest> orderItems;
}
