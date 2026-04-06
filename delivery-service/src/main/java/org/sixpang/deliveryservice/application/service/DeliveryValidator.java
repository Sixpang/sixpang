package org.sixpang.deliveryservice.application.service;

import java.util.UUID;

public interface DeliveryValidator {
    void validate(UUID departureHubId, UUID arrivalHubId);
}
