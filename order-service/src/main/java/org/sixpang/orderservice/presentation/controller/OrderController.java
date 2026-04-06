package org.sixpang.orderservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.orderservice.application.service.OrderService;
import org.sixpang.orderservice.exception.OrderErrorCode;
import org.sixpang.orderservice.presentation.dto.CreateOrderRequest;
import org.sixpang.orderservice.presentation.dto.OrderDetailResponse;
import org.sixpang.orderservice.presentation.dto.OrderPageResponse;
import org.sixpang.orderservice.presentation.dto.UpdateOrderRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping
    public ResponseEntity<OrderDetailResponse> createOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    // 주문 단건
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    // 주문 전체 목록 조회 -> 마스터만
    @GetMapping
    public ResponseEntity<OrderPageResponse> getOrders(
            @RequestHeader("X-User-Id") String role,
            @PageableDefault(size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        if (!"MASTER".equals(role)) {
            throw new CustomException(OrderErrorCode.ORDER_FORBIDDEN);
        }
        return ResponseEntity.ok(orderService.getOrders(pageable));
    }

    // 공급 업체별 주문 목록 조회
    @GetMapping("/supllier/{supplierId}")
    public ResponseEntity<OrderPageResponse> getOrdersBySupplierId(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID supplierId,
            @PageableDefault(size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrdersBySupplierId(supplierId, pageable));
    }

    // 수령 업체별 주문 목록 조회
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<OrderPageResponse> getOrdersByReceiverId(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID receiverId,
            @PageableDefault(size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrdersByReceiverId(receiverId, pageable));
    }

    // 주문 수정
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> updateOrders(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, request));
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") UUID deletedBy
            // 게이트웨이에서 헤더로 넘겨주는 유저 ID
    ) {
        orderService.deleteOrder(orderId, deletedBy);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


}
