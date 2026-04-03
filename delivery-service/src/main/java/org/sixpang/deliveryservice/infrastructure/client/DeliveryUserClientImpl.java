package org.sixpang.deliveryservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.service.DeliveryUserClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeliveryUserClientImpl implements DeliveryUserClient {
    @Value("${feature.user-service}")
    boolean featureFlag;

    private final UserClient userClient;
    private final UserMockClient userMockClient;

    public void checkExists(UUID userId) {
        if(featureFlag) {
            userMockClient.checkExists(userId);
        } else {
            userClient.checkExists(userId);
        }
    }
}
