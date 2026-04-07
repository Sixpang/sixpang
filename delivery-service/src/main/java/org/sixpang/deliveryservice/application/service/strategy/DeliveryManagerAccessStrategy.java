package org.sixpang.deliveryservice.application.service.strategy;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DeliveryManagerAccessStrategy implements DeliveryAccessStrategy {
    @Override
    public boolean supports(String role) {
        return "DELIVERY_MANAGER".equals(role);
    }

    @Override
    public void validateAccess(Delivery delivery, UUID requestUserId, UUID requestHubId) {
        boolean isAssigned = delivery.getDeliveryRoutes().stream()
                .anyMatch(route -> isManagerAssignedToRoute(route, requestUserId));

        if (!isAssigned) {
            throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
        }
    }

    private boolean isManagerAssignedToRoute(org.sixpang.deliveryservice.domain.model.entity.DeliveryRoute route, UUID userId) {
        return route.getHubDeliveryManagerId().equals(userId);
    }
}
