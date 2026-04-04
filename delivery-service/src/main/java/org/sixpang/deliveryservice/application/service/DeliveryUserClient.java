package org.sixpang.deliveryservice.application.service;

import java.util.UUID;

public interface DeliveryUserClient {
    void checkExists(UUID userId);
}
