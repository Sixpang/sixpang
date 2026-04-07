package org.sixpang.deliveryservice.application.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.deliveryservice.application.service.client.DeliveryHubClient;
import org.sixpang.deliveryservice.exception.DeliveryErrorCode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HubExistenceValidator implements DeliveryValidator {
    private final DeliveryHubClient hubClient;

    @Override
    public void validate(UUID departureHubId, UUID arrivalHubId) {
        validateHubExists(departureHubId, "출발");
        validateHubExists(arrivalHubId, "도착");
    }

    private void validateHubExists(UUID hubId, String hubType) {
        try {
            hubClient.checkExists(hubId);
        } catch (FeignException.NotFound e) {
            throw new CustomException(
                    DeliveryErrorCode.HUB_NOT_FOUND);
        }
    }
}
