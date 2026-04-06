package org.sixpang.orderservice.presentation.controller;

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
public class OrderController {

    private final OrderService orderService;

//    @GetMapping("/order")
//    public String createOrder() {
//        orderService.createOrder();
//        return "주문 생성 완료";
//    }

    // 주문 생성 - 모든 로그인 사용자
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDetailResponse>> createOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 생성 성공", orderService.createOrder(request))
        );
    }

    // 주문 단건 조회 - 모든 로그인 사용자 (본인 주문만)
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 단건 조회 성공", orderService.getOrder(orderId, user))
        );
    }

    // 전체 목록 조회 - MASTER만
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

    // 공급 업체별 주문 목록 조회
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersBySupplierId(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID supplierId,
            OrderPageRequest pageRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("공급업체별 주문 조회 성공",
                        orderService.getOrdersBySupplierId(supplierId, pageRequest.toPageable()))
        );
    }

    // 수령 업체별 주문 목록 조회
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByReceiverId(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID receiverId,
            OrderPageRequest pageRequest
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("수령업체별 주문 조회 성공",
                        orderService.getOrdersByReceiverId(receiverId, pageRequest.toPageable()))
        );
    }

    // 주문 수정
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 수정 성공", orderService.updateOrder(orderId, request, user))
        );
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID orderId
    ) {
        orderService.deleteOrder(orderId, user);
        return ResponseEntity.ok(ApiResponse.of("주문 삭제 성공", null));
    }
}
