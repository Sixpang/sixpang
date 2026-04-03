package org.sixpang.deliveryservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {
    @GetMapping("/api/hubs/{id}")
    void checkExists(@PathVariable UUID hubId);
}
