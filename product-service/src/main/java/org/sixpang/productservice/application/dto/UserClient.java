package org.sixpang.productservice.application.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/internal/{id}")
    UserPermissionInfo getUserPermissionInfo(@PathVariable("id") UUID id);
}

