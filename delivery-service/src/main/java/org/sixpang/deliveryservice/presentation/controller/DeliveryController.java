package org.sixpang.deliveryservice.presentation.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryQueryService deliveryQueryService;
    private final DeliveryCreationService deliveryCreationService;

    @PostMapping
    public ResponseEntity<UUID> createDelivery(@RequestBody OrderCreatedEvent event) {
        UUID deliveryId = deliveryCreationService.createDeliveryFromOrder(event);
        return ResponseEntity.ok(deliveryId);
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryDetailResponse> getDelivery(
            @PathVariable UUID deliveryId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        DeliveryDetailResponse response = deliveryQueryService.getDeliveryById(
                deliveryId, role, userId, hubId
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{deliveryId}/routes")
    public ResponseEntity<DeliveryWithRoutesResponse> getDeliveryWithRoutes(
            @PathVariable UUID deliveryId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        DeliveryWithRoutesResponse response = deliveryQueryService.getDeliveryWithRoutes(
                deliveryId, role, userId, hubId
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DeliveryDetailResponse>> searchDeliveries(
            DeliverySearchCondition condition,
            Pageable pageable,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        Page<DeliveryDetailResponse> response = deliveryQueryService.searchDeliveries(
                condition, pageable, role, userId, hubId
        );
        return ResponseEntity.ok(response);
    }
}