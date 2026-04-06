package org.sixpang.companyservice.application.dto;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;


@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/internal/users/{userId}/permission-info")
    UserPermissionInfo getUserPermissionInfo(@PathVariable UUID userId);
}
