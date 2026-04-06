package org.sixpang.hubservice.presentation;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.hubservice.application.dto.AvailableRouteResponseDto;
import org.sixpang.hubservice.application.dto.DirectRouteResponseDto;
import org.sixpang.hubservice.application.dto.OptimalRouteResponseDto;
import org.sixpang.hubservice.application.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hub-routes")
public class RouteController {
    private final RouteService routeService;

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<AvailableRouteResponseDto>>> getAvailableRoutes(
            @RequestParam UUID departureHubId) {

        List<AvailableRouteResponseDto> responseDto = routeService.getAvailableRoutes(departureHubId);
        return ResponseEntity.ok(ApiResponse.of("허브에서 이동 가능한 경로 목록이 조회되었습니다.", responseDto));
    }

    @GetMapping("/direct")
    public ResponseEntity<ApiResponse<DirectRouteResponseDto>> getDirectRoute(
            @RequestParam UUID departureHubId,
            @RequestParam UUID arrivalHubId) {

        DirectRouteResponseDto responseDto = routeService.getDirectRoutes(departureHubId, arrivalHubId);
        return ResponseEntity.ok(ApiResponse.of("두 허브 간 경로가 조회되었습니다.", responseDto));
    }

    @GetMapping("/optimal")
    public ResponseEntity<ApiResponse<OptimalRouteResponseDto>> findOptimalRoute(
            @RequestParam UUID departureHubId,
            @RequestParam UUID arrivalHubId) {

        OptimalRouteResponseDto responseDto = routeService.findOptimalRoute(departureHubId, arrivalHubId);
        return ResponseEntity.ok(ApiResponse.of("최적 이동 경로가 조회되었습니다.", responseDto));
    }
}
