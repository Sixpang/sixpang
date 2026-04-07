package org.sixpang.deliveryservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.DeliveryRouteRequest;
import org.sixpang.deliveryservice.application.dto.DeliveryRouteResponse;
import org.sixpang.deliveryservice.application.service.service.DeliveryRouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries/routes")
@RequiredArgsConstructor
@Tag(name = "배송", description = "배송 관련 API")
public class DeliveryRouteController {
    private final DeliveryRouteService deliveryRouteService;

    //배송 경로 생성
    @Operation(
            summary = "배송 경로 생성",
            description = "새로운 배송 경로를 생성합니다. <br>" +
                    "배송 생성과 동시에 모든 경로가 생성되며, 허브 간 이동 거리를 계산하여 예상 소요시간을 측정합니다. <br>" +
                    "출발 및 목적지 허브 ID의 존재 여부를 확인합니다. (주문/배송 생성 실패 시 롤백)"
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<UUID> createRoute(@RequestBody DeliveryRouteRequest request) {
        UUID routeId = deliveryRouteService.createDeliveryRoute(
                request.deliveryId(),
                request.hubSequence(),
                request.departureHub(),
                request.arrivalHub(),
                request.hubDeliveryManagerId()
        );
        return ResponseEntity.ok(routeId);
    }

    //배송경로 조회
    @Operation(
            summary = "특정 배송의 전체 경로 조회",
            description = "특정 배송 ID에 연관된 모든 배송 경로(과정)를 조회하여 각 경로를 추적합니다. <br>" +
                    "권한에 따라 마스터(전체), 허브 관리자(담당 허브), 배송/업체 관리자(본인 건)에 한해 조회 가능합니다."
    )
    @GetMapping("/{deliveryId}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'COMPANY_MANAGER')")
    public ResponseEntity<List<DeliveryRouteResponse>> getRoutesByDelivery(@PathVariable UUID deliveryId) {
        List<DeliveryRouteResponse> routes = deliveryRouteService.getRoutesByDelivery(deliveryId);
        return ResponseEntity.ok(routes);
    }

    //상세 조회
    @Operation(
            summary = "배송 경로 상세 조회",
            description = "단일 배송 경로 건에 대한 상세 정보를 조회합니다."
    )
    @GetMapping("/{routeId}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'COMPANY_MANAGER')")
    public ResponseEntity<DeliveryRouteResponse> getRouteDetail(@PathVariable UUID routeId) {
        DeliveryRouteResponse response = deliveryRouteService.getRouteDetail(routeId);
        return ResponseEntity.ok(response);
    }
}