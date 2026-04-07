package org.sixpang.orderservice.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.orderservice.application.service.OrderService;
import org.sixpang.orderservice.presentation.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "주문", description = "주문 관련 API")  // ← 추가
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "주문 생성",
            description = "새로운 주문을 생성합니다. <br>" +
                    "로그인한 모든 사용자가 접근 가능합니다. <br>" +
                    "주문 생성 시 상품 재고를 확인하고 재고를 감소시킵니다. <br>" +
                    "주문 생성 이벤트가 Kafka로 발행됩니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 생성 성공", orderService.createOrder(request))
        );
    }

    @Operation(
            summary = "주문 단건 조회",
            description = "주문 ID로 단건 조회합니다. <br>" +
                    "MASTER는 모든 주문을 조회할 수 있습니다. <br>" +
                    "일반 사용자는 본인이 생성한 주문만 조회 가능합니다."
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @Parameter(description = "조회할 주문 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 단건 조회 성공", orderService.getOrder(orderId, user))
        );
    }

    @Operation(
            summary = "전체 주문 목록 조회",
            description = "모든 주문을 페이지 단위로 조회합니다. <br>" +
                    "MASTER 권한을 가진 사용자만 접근 가능합니다."
    )
    @GetMapping
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrders(
            OrderPageRequest pageRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 전체 목록 조회 성공",
                        orderService.getOrders(pageRequest.toPageable()))
        );
    }

    @Operation(
            summary = "공급업체별 주문 목록 조회",
            description = "특정 공급업체 ID로 주문 목록을 페이지 단위로 조회합니다."
    )
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersBySupplierId(
            @AuthenticationPrincipal UserPrincipal user,
            @Parameter(description = "공급업체 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID supplierId,
            OrderPageRequest pageRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("공급업체별 주문 조회 성공",
                        orderService.getOrdersBySupplierId(supplierId, pageRequest.toPageable()))
        );
    }

    @Operation(
            summary = "수령업체별 주문 목록 조회",
            description = "특정 수령업체 ID로 주문 목록을 페이지 단위로 조회합니다."
    )
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByReceiverId(
            @AuthenticationPrincipal UserPrincipal user,
            @Parameter(description = "수령업체 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID receiverId,
            OrderPageRequest pageRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("수령업체별 주문 조회 성공",
                        orderService.getOrdersByReceiverId(receiverId, pageRequest.toPageable()))
        );
    }

    @Operation(
            summary = "주문 수정",
            description = "주문의 납입 기한을 수정합니다. <br>" +
                    "MASTER는 모든 주문을 수정할 수 있습니다. <br>" +
                    "일반 사용자는 본인 주문만 수정 가능합니다. <br>" +
                    "CONFIRMED 상태인 주문만 수정 가능합니다."
    )
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @Parameter(description = "수정할 주문 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 수정 성공", orderService.updateOrder(orderId, request, user))
        );
    }

    @Operation(
            summary = "주문 삭제",
            description = "주문을 소프트 삭제합니다. <br>" +
                    "MASTER는 모든 주문을 삭제할 수 있습니다. <br>" +
                    "일반 사용자는 본인 주문만 삭제 가능합니다."
    )
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @Parameter(description = "삭제할 주문 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable UUID orderId
    ) {
        orderService.deleteOrder(orderId, user);
        return ResponseEntity.ok(ApiResponse.of("주문 삭제 성공", null));
    }
}