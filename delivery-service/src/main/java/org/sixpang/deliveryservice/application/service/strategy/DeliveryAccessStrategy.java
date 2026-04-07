package org.sixpang.deliveryservice.application.service.strategy;

import org.sixpang.deliveryservice.domain.model.entity.Delivery;

import java.util.UUID;

public interface DeliveryAccessStrategy {
    boolean supports(String role);

    void validateAccess(Delivery delivery, UUID requestUserId, UUID requestHubId);
}
