package org.sixpang.deliveryservice.application.service;

import java.util.UUID;

public interface DeliveryHubClient {
    void checkExists(UUID hubId);
}
