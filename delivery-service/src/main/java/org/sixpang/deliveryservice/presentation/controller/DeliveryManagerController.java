package org.sixpang.deliveryservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerCreateRequest;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerUpdateRequest;
import org.sixpang.deliveryservice.application.service.service.DeliveryManagerService;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries/managers")
@RequiredArgsConstructor
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    // 생성
    @PostMapping
    public ResponseEntity<DeliveryManagerResponse> create(
            @RequestBody @Valid DeliveryManagerCreateRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryManagerService.create(request, role, userId));
    }

    // 상세조회
    @GetMapping("/{managerId}")
    public ResponseEntity<DeliveryManagerResponse> getById(
            @PathVariable UUID managerId,
            @RequestParam DeliveryManagerType type,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.ok(deliveryManagerService.getById(managerId, type, role, userId));
    }

    // 수정
    @PatchMapping("/{managerId}")
    public ResponseEntity<DeliveryManagerResponse> update(
            @PathVariable UUID managerId,
            @RequestParam DeliveryManagerType type,
            @RequestBody @Valid DeliveryManagerUpdateRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        DeliveryManagerResponse response = deliveryManagerService.update(
                managerId,
                type,
                request,
                role,
                userId,
                hubId
        );

        return ResponseEntity.ok(response);
    }

    // 삭제
    @DeleteMapping("/{managerId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID managerId,
            @RequestParam DeliveryManagerType type,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        deliveryManagerService.delete(managerId, type, role, userId, hubId);
        return ResponseEntity.noContent().build();
    }

    // 배송 담당자 배정 (허브)
    @PostMapping("/assign/hub")
    public ResponseEntity<DeliveryManagerResponse> assignHubManager() {
        return ResponseEntity.ok(DeliveryManagerResponse.fromHub(
                deliveryManagerService.assignHubManager()));
    }

    // 배송 담당자 배정 (업체)
    @PostMapping("/assign/company")
    public ResponseEntity<DeliveryManagerResponse> assignCompanyManager(
            @RequestParam UUID hubId
    ) {
        return ResponseEntity.ok(DeliveryManagerResponse.fromCompany(
                deliveryManagerService.assignCompanyManager(hubId)));
    }
}