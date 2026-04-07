package org.sixpang.orderservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderRequest {

    private Timestamp deadlineAt;
}
