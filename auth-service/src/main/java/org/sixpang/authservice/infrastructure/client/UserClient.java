package org.sixpang.authservice.infrastructure.client;

import org.sixpang.commonserver.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.sixpang.authservice.infrastructure.client.dto.UserAuthDto;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/email")
    ApiResponse<UserAuthDto> getUserByEmail(@RequestParam String email);
}
