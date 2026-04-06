package org.sixpang.deliveryservice.application.service.client;

import java.util.UUID;

public interface DeliveryUserClient {
    void checkExists(UUID userId);
}
