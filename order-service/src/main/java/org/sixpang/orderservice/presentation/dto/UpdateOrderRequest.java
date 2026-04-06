package org.sixpang.orderservice.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
public class UpdateOrderRequest {

    private Timestamp deadlineAt;
}
