package org.sixpang.deliveryservice.application.service.client;

import java.util.UUID;

public interface DeliveryHubClient {
    void checkExists(UUID hubId);
}
