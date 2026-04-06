package org.sixpang.userservice.application.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-service", contextId = "hubClient")
public interface HubClient {

    @GetMapping("/api/hubs/{id}/exists")
    boolean exists(@PathVariable UUID id);
}
