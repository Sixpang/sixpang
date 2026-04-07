package org.sixpang.deliveryservice.application.service.strategy;

import org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute;

import java.util.List;
import java.util.UUID;

public interface RouteCreationStrategy {
    List<DeliveryRoute> createRoutes(UUID departureHubId, UUID arrivalHubId);
}
