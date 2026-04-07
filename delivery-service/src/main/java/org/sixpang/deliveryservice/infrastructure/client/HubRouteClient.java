package org.sixpang.deliveryservice.infrastructure.client;

import org.sixpang.deliveryservice.application.dto.HubRouteInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "hub-service", contextId = "hubRouteClient")
public interface HubRouteClient {
    @GetMapping("/api/hubs/routes/optimal")
    HubRouteInfo findOptimalRoute(
            @RequestParam UUID departureHubId,
            @RequestParam UUID arrivalHubId
    );
}