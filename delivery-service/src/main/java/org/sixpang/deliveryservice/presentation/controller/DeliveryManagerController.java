package org.sixpang.deliveryservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerCreateRequest;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerResponse;
import org.sixpang.deliveryservice.application.service.DeliveryManagerService;
import org.sixpang.deliveryservice.application.dto.DeliveryManagerUpdateRequest;
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

    //생성
    @PostMapping
    public ResponseEntity<DeliveryManagerResponse> create(
            @RequestBody @Valid DeliveryManagerCreateRequest request
            //수정예약:회원 인증/인가 완성후 수정
            ){
        String role = "MASTER";
        UUID requestUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryManagerService.create(request, role, requestUserId));
    }

    // 상세조회
    @GetMapping("/{managerId}")
    public ResponseEntity<DeliveryManagerResponse> getById(
            @PathVariable UUID managerId,
            @RequestParam DeliveryManagerType type
    ) {
        // 수정예약: 회원 인증/인가 완성 후 수정
        String role = "MASTER";
        UUID requestUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");

        return ResponseEntity.ok(deliveryManagerService.getById(managerId, type, role, requestUserId));
    }

    // 수정
    @PatchMapping("/{managerId}")
    public ResponseEntity<DeliveryManagerResponse> update(
            @PathVariable UUID managerId,
            @RequestParam DeliveryManagerType type,
            @RequestBody @Valid DeliveryManagerUpdateRequest request
    ) {
        // 수정예약: 회원 인증/인가 완성 후 수정
        String role = "MASTER";
        UUID requestUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID requestHubId = null; // 허브 매니저일 경우 해당 허브 ID가 들어가야 함

        return ResponseEntity.ok(deliveryManagerService.update(managerId, type, request, role, requestUserId, requestHubId));
    }

    // 삭제
    @DeleteMapping("/{managerId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID managerId,
            @RequestParam DeliveryManagerType type
    ) {
        // 수정예약: 회원 인증/인가 완성 후 수정
        String role = "MASTER";
        UUID requestUserId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        UUID requestHubId = null;

        deliveryManagerService.delete(managerId, type, role, requestUserId, requestHubId);
        return ResponseEntity.noContent().build();
    }

    //배송 담당자 배정 테스트

    // 1. 허브 배송 담당자 배정 (전체 리스트에서 순번대로)
    @PostMapping("/assign/hub")
    public ResponseEntity<DeliveryManagerResponse> assignHubManager() {
        // 내부적으로 Redis를 통해 순번을 돌리고 상태를 ON_TASK로 바꿉니다.
        return ResponseEntity.ok(DeliveryManagerResponse.fromHub(deliveryManagerService.assignHubManager()));
    }

    // 2. 업체 배송 담당자 배정 (특정 허브 소속 담당자 중 순번대로)
    @PostMapping("/assign/company")
    public ResponseEntity<DeliveryManagerResponse> assignCompanyManager(
            @RequestParam UUID hubId
    ) {
        // 특정 허브 ID를 받아 해당 큐에서 배정
        return ResponseEntity.ok(DeliveryManagerResponse.fromCompany(deliveryManagerService.assignCompanyManager(hubId)));
    }

}
