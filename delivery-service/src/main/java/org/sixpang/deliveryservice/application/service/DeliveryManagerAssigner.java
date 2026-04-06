package org.sixpang.deliveryservice.application.service;

import java.util.List;
import java.util.Optional;

public interface DeliveryManagerAssigner {
    Optional<String> getNextIdWithRotation(String key);

    Optional<String> getNextId(String key);

    void refreshCache(String key, List<String> ids);
}
