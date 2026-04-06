package org.sixpang.deliveryservice.application.service.client;

import org.sixpang.deliveryservice.application.dto.HubRouteInfo;

import java.util.UUID;

public interface DeliveryHubRouteClient {
    HubRouteInfo getRouteInfo(UUID id);
}
