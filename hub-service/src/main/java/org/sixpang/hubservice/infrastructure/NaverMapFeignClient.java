package org.sixpang.hubservice.infrastructure;

import org.sixpang.hubservice.application.dto.DirectionsResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "naver-map-client", url = "${naver.api.directions-url}")
public interface NaverMapFeignClient {
    @GetMapping("/driving")
    DirectionsResponseDto getRoute(
            @RequestHeader("x-ncp-apigw-api-key-id") String clientId,
            @RequestHeader("x-ncp-apigw-api-key") String clientSecret,
            @RequestParam("start") String start,
            @RequestParam("goal") String goal
    );
}
