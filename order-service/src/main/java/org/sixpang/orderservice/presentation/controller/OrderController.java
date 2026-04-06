package org.sixpang.orderservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.orderservice.application.service.OrderService;
import org.sixpang.orderservice.presentation.dto.CreateOrderRequest;
import org.sixpang.orderservice.presentation.dto.OrderDetailResponse;
import org.sixpang.orderservice.presentation.dto.OrderResponse;
import org.sixpang.orderservice.presentation.dto.UpdateOrderRequest;
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

    // 주문 단건 조회 - 모든 로그인 사용자
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 단건 조회 성공", orderService.getOrder(orderId))
        );
    }

    // 전체 목록 조회 - MASTER만
    @GetMapping
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 전체 목록 조회 성공",
                        orderService.getOrders(page, size, sortBy, sortDir))
        );
    }

    // 공급업체별 조회 - 모든 로그인 사용자
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersBySupplierId(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID supplierId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("공급업체별 주문 조회 성공",
                        orderService.getOrdersBySupplierId(supplierId, page, size, sortBy, sortDir))
        );
    }

    // 수령업체별 조회 - 모든 로그인 사용자
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getOrdersByReceiverId(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID receiverId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("수령업체별 주문 조회 성공",
                        orderService.getOrdersByReceiverId(receiverId, page, size, sortBy, sortDir))
        );
    }

    // 주문 수정 - 모든 로그인 사용자
    @PutMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("주문 수정 성공", orderService.updateOrder(orderId, request))
        );
    }

    // 주문 삭제 - 모든 로그인 사용자
    // @AuthenticationPrincipal은 파라미터 앞에 붙이는 거예요!
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID orderId
    ) {
        orderService.deleteOrder(orderId, user.getUserId());
        return ResponseEntity.ok(ApiResponse.of("주문 삭제 성공", null));
    }
}
