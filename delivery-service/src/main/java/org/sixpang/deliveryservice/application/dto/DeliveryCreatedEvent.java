package org.sixpang.deliveryservice.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryCreatedEvent {

    private UUID deliveryId;
    private UUID orderId;
    private UUID departureHub;
    private UUID arrivalHub;
    private UUID hubDeliveryManagerId;
    private String status;
    
    private String eventType = "DELIVERY_CREATED";
    private LocalDateTime eventTimestamp = LocalDateTime.now();
}