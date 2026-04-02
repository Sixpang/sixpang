package org.sixpang.deliveryservice.application;

import org.antlr.v4.runtime.misc.NotNull;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;

import java.util.UUID;

public record DeliveryManagerCreateRequest(
        @NotNull UUID userId, @NotNull DeliveryManagerType type, UUID hubId
        ) { }
