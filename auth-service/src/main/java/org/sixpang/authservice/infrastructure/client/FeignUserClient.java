package org.sixpang.authservice.infrastructure.client;

import org.sixpang.commonserver.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sixpang.authservice.application.client.dto.UserAuthDto;

@org.springframework.cloud.openfeign.FeignClient(name = "user-service")
public interface FeignUserClient {

    @GetMapping("/api/users/email")
    ApiResponse<UserAuthDto> getUserByEmail(@RequestParam String email);
}
