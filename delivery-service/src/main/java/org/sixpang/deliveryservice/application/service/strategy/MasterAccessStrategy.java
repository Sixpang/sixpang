package org.sixpang.deliveryservice.application.service.strategy;

import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MasterAccessStrategy implements DeliveryAccessStrategy {
    @Override
    public boolean supports(String role) {
        return "MASTER".equals(role);
    }

    @Override
    public void validateAccess(Delivery delivery, UUID requestUserId, UUID requestHubId) {
    }
}
