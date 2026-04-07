package org.sixpang.deliveryservice.application.service.strategy;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.domain.model.entity.Delivery;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HubManagerAccessStrategy implements DeliveryAccessStrategy {
    @Override
    public boolean supports(String role) {
        return "HUB_MANAGER".equals(role);
    }

    @Override
    public void validateAccess(Delivery delivery, UUID requestUserId, UUID requestHubId) {
        if (requestHubId == null) {
            throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
        }

        boolean hasAccess = delivery.getDepartureHub().equals(requestHubId)
                || delivery.getArrivalHub().equals(requestHubId);

        if (!hasAccess) {
            throw new CustomException(DeliveryErrorCode.ACCESS_DENIED);
        }
    }
}
