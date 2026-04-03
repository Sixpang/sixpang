package org.sixpang.deliveryservice.application.dto;

import jakarta.validation.constraints.NotNull;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerCreateRequest(
        @NotNull UUID userId, @NotNull DeliveryManagerType type, UUID hubId
        ) { }
