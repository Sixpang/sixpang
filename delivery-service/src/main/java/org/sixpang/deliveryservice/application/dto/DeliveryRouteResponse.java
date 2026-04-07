package org.sixpang.deliveryservice.application.dto;

import org.sixpang.deliveryservice.domain.model.enums.DeliveryRouteStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryRouteResponse(UUID id,
                                    Integer hubSequence,
                                    UUID departureHub,
                                    UUID arrivalHub,
                                    BigDecimal estimatedDistance,
                                    Long estimatedTime,
                                    DeliveryRouteStatus status,
                                    LocalDateTime actualAt) {
}
