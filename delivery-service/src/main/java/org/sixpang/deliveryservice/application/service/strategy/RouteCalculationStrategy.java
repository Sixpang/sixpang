package org.sixpang.deliveryservice.application.service.strategy;

import org.sixpang.deliveryservice.application.dto.RouteCalculationResult;

import java.util.UUID;

public interface RouteCalculationStrategy {
    RouteCalculationResult calculate(UUID departureHubId, UUID arrivalHubId);
}