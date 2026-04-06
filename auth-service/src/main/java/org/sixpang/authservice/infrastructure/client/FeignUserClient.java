package org.sixpang.authservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "user-service")
public interface FeignUserClient {

    @GetMapping("/api/users/email")
    Map<String, Object> getUserByEmail(@RequestParam String email);
}