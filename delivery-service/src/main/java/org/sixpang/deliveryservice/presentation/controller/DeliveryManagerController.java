// DeliveryManagerController.java - 배송 담당자 관리만
package org.sixpang.deliveryservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerCreateRequest;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerSearchCondition;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerUpdateRequest;
import org.sixpang.deliveryservice.application.service.service.DeliveryManagerService;
import org.sixpang.deliveryservice.domain.model.enums.DeliveryManagerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/delivery-managers")
@RequiredArgsConstructor
@Tag(name = "배송 담당자", description = "배송 담당자 관리 API")
public class DeliveryManagerController {

    private final DeliveryManagerService deliveryManagerService;

    // 생성
    @Operation(
            summary = "배송 담당자 생성",
            description = "새로운 배송 담당자를 생성합니다. <br>" +
                    "마스터 관리자만 접근 가능합니다. <br>" +
                    "생성 시 USER ID, 소속 허브 ID 존재 여부를 확인하며, 배송 담당자 중복을 검사합니다. <br>" +
                    "배송 담당자 지정 시 반드시 담당 타입(허브/업체)을 지정해야 합니다."
    )
    @PostMapping
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<DeliveryManagerResponse> create(
            @RequestBody @Valid DeliveryManagerCreateRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(deliveryManagerService.create(request, role, userId));
    }

    // 상세조회
    @Operation(
            summary = "배송 담당자 상세 조회",
            description = "배송 담당자의 상세 정보를 조회합니다. <br>" +
                    "마스터: 생성/수정/삭제/조회 가능 <br>" +
                    "허브 관리자: 담당 허브에 한해 생성/수정/삭제/조회 가능 <br>" +
                    "배송 담당자: 본인 정보 조회 가능"
    )
    @GetMapping("/{managerId}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER', 'COMPANY_MANAGER')")
    public ResponseEntity<DeliveryManagerResponse> getById(
            @PathVariable UUID managerId,
            @RequestParam DeliveryManagerType type,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.ok(deliveryManagerService.getById(managerId, type, role, userId));
    }

    // 목록 조회 (검색)
    @Operation(
            summary = "배송 담당자 목록 조회",
            description = "배송 담당자 목록을 조회합니다. <br>" +
                    "검색 조회시 카테고리: SLACK ID/허브 ID/담당 타입/이름"
    )
    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER', 'DELIVERY_MANAGER')")
    public ResponseEntity<Page<DeliveryManagerResponse>> search(
            DeliveryManagerSearchCondition condition,
            Pageable pageable,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        return ResponseEntity.ok(deliveryManagerService.search(condition, pageable, role, userId));
    }

    // 수정
    @Operation(
            summary = "배송 담당자 수정",
            description = "배송 담당자 정보를 수정합니다. <br>" +
                    "마스터 관리자 및 담당 허브 관리자만 수정 가능합니다. <br>" +
                    "허브 관리자 수정 시 소속 허브 ID가 존재하는지 확인합니다."
    )
    @PatchMapping("/{managerId}")
    @PreAuthorize("hasAnyRole('MASTER', 'HUB_MANAGER')")
    public ResponseEntity<DeliveryManagerResponse> update(
            @PathVariable UUID managerId,
            @RequestParam DeliveryManagerType type,
            @RequestBody @Valid DeliveryManagerUpdateRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader(value = "X-Hub-Id", required = false) UUID hubId
    ) {
        return ResponseEntity.ok(deliveryManagerService.update(
                managerId,
                type,
                request,
                role,
                userId,
                hubId
        ));
    }

    // 삭제
    @Operation(
            summary = "배송 담당자 삭제",
            description = "배송 담당자를 삭제합니다. <br>" +
                    "마스터 관리자 및 담당 허브 관리자에 한해 삭제 가능합니다."
    )
    @DeleteMapping("/{managerId}")
    @PreAuthorize("hasRole('MASTER')")
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
}