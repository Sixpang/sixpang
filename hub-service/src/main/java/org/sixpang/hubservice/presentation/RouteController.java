package org.sixpang.hubservice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/hubs/routes")
@Tag(name = "루트", description = "허브 간 이동경로 관련 API")
public class RouteController {
    private final RouteService routeService;

    @Operation(
            summary = "특정 허브에서 이동 가능한 경로 조회",
            description = "특정 허브에서 이동 가능한 경로를 조회합니다. <br>" +
                    "로그인한 모든 사용자가 접근 가능합니다. <br>" +
                    "거리에 상관없이 해당 허브에서 이동 가능한 모든 경로가 조회됩니다. <br>" +
                    "허브가 운영 중지/운영 중단 혹은 삭제될 시 해당 허브는 조회되지 않습니다."
    )
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<AvailableRouteResponseDto>>> getAvailableRoutes(
            @RequestParam UUID departureHubId) {

        List<AvailableRouteResponseDto> responseDto = routeService.getAvailableRoutes(departureHubId);
        return ResponseEntity.ok(ApiResponse.of("허브에서 이동 가능한 경로 목록이 조회되었습니다.", responseDto));
    }

    @Operation(
            summary = "특정 허브 간 이동경로 조회",
            description = "특정 허브 간 이동경로를 조회합니다. <br>" +
                    "로그인한 모든 사용자가 접근 가능합니다. <br>" +
                    "두 허브 간 이동거리(km)와 소요시간(H)이 반환됩니다. <br>" +
                    "허브가 운영 중지/운영 중단 혹은 삭제될 시 해당 허브는 조회되지 않고, 이동 경로에서도 제외됩니다."
    )
    @GetMapping("/direct")
    public ResponseEntity<ApiResponse<DirectRouteResponseDto>> getDirectRoute(
            @RequestParam UUID departureHubId,
            @RequestParam UUID arrivalHubId) {

        DirectRouteResponseDto responseDto = routeService.getDirectRoutes(departureHubId, arrivalHubId);
        return ResponseEntity.ok(ApiResponse.of("두 허브 간 경로가 조회되었습니다.", responseDto));
    }

    @Operation(
            summary = "허브 간 최적 이동 경로 조회",
            description = "허브 간 최적의 이동 경로를 조회합니다. <br>" +
                    "로그인한 모든 사용자가 접근 가능합니다. <br>" +
                    "P2P + Hub to Hub Relay(Dijkstra 알고리즘 사용) 방식을 사용합니다. <br>" +
                    "허브가 운영 중지/운영 중단 혹은 삭제될 시 해당 허브는 조회되지 않고, 이동 경로에서도 제외됩니다."
    )
    @GetMapping("/optimal")
    public ResponseEntity<ApiResponse<OptimalRouteResponseDto>> findOptimalRoute(
            @RequestParam UUID departureHubId,
            @RequestParam UUID arrivalHubId) {

        OptimalRouteResponseDto responseDto = routeService.findOptimalRoute(departureHubId, arrivalHubId);
        return ResponseEntity.ok(ApiResponse.of("최적 이동 경로가 조회되었습니다.", responseDto));
    }
}
