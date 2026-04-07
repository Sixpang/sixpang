package org.sixpang.deliveryservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.DeliveryDetailResponse;
import org.sixpang.deliveryservice.application.dto.DeliverySearchCondition;
import org.sixpang.deliveryservice.application.dto.DeliveryWithRoutesResponse;
import org.sixpang.deliveryservice.application.dto.OrderCreatedEvent;
import org.sixpang.deliveryservice.application.service.service.DeliveryCreationService;
import org.sixpang.deliveryservice.application.service.service.DeliveryQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(name = "배송", description = "배송 관리 API")
public class DeliveryController {

    private final DeliveryQueryService deliveryQueryService;
    private final DeliveryCreationService deliveryCreationService;

    // 배송 생성
    @Operation(
            summary = "배송 생성",
            description = "주문 이벤트를 기반으로 새로운 배송 및 경로를 생성합니다."
    )
    @PostMapping
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<UUID> createDelivery(@RequestBody OrderCreatedEvent event) {
        return ResponseEntity.ok(deliveryCreationService.createDeliveryFromOrder(event));
    }

    // 배송 상세 조회
    @Operation(
            summary = "배송 상세 조회",
            description = "배송의 상세 정보를 조회합니다. <br>" +
                    "마스터(모든 건), 허브 관리자(담당 허브), 배송 담당자(본인 배송), 업체 관리자(본인 건)에 한해 조회 가능합니다."
    )
    @GetMapping("/{deliveryId}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'COMPANY_MANAGER')")
    public ResponseEntity<DeliveryDetailResponse> getDelivery(
            @PathVariable UUID deliveryId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        return ResponseEntity.ok(
                deliveryQueryService.getDeliveryById(deliveryId, role, userId, hubId)
        );
    }

    // 배송 목록 조회
    @Operation(
            summary = "배송 목록 조회",
            description = "배송 목록을 조회합니다. <br>" +
                    "마스터: 모든 조회 가능 <br>" +
                    "허브 관리자: 담당 허브에 한해 가능 <br>" +
                    "배송 담당자: 본인 배송에 한해 가능 <br>" +
                    "업체 관리자: 본인 건에 한해 가능"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'COMPANY_MANAGER')")
    public ResponseEntity<Page<DeliveryDetailResponse>> getDeliveries(
            DeliverySearchCondition condition,
            Pageable pageable,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        return ResponseEntity.ok(
                deliveryQueryService.searchDeliveries(condition, pageable, role, userId, hubId)
        );
    }

    // 배송 경로 추적
    @Operation(
            summary = "배송 경로 추적",
            description = "배송 과정에서 발생한 각 경로를 추적합니다."
    )
    @GetMapping("/{deliveryId}/routes")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'COMPANY_MANAGER')")
    public ResponseEntity<DeliveryWithRoutesResponse> getDeliveryRoutes(
            @PathVariable UUID deliveryId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        return ResponseEntity.ok(
                deliveryQueryService.getDeliveryWithRoutes(deliveryId, role, userId, hubId)
        );
    }
}