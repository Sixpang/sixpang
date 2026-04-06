package org.sixpang.deliveryservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.service.client.DeliveryHubClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryHubClientImpl implements DeliveryHubClient {
    @Value("true")
    boolean featureFlag;

    private final HubClient hubClient;
    private final HubMockClient hubMockClient;

    public void checkExists(UUID hubId) {
        if (featureFlag) {
            hubMockClient.checkExists(hubId);
        } else {
            hubClient.checkExists(hubId);
        }
    }

}
