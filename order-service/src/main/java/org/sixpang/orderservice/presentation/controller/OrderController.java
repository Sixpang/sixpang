package org.sixpang.orderservice.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.ApiResponse;
import org.sixpang.orderservice.application.service.OrderService;
import org.sixpang.orderservice.application.service.OrderServiceImpl;
import org.sixpang.orderservice.presentation.dto.OrderRequestDto;
import org.sixpang.orderservice.presentation.dto.OrderResponseDto;
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
    public ResponseEntity<OrderResponseDto.OrderDetailResponse> createOrder(
            @Valid @RequestBody OrderRequestDto.CreateOrderRequest request
            ) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    // 주문 단건
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto.OrderDetailResponse> getOrder(
            @PathVariable UUID orderId
            ) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    // 주문 전체 목록 조회
    @GetMapping
    public ResponseEntity<OrderResponseDto.OrderDetailResponse> getOrders(
            // 기본 페이징 10개씩, 최신순
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrders(pageable));
    }

    // 공급 업체별 주문 목록 조회
    @GetMapping("/supllier/{supplierId}")
    public ResponseEntity<OrderResponseDto.OrderDetailResponse> getOrdersBySupplierId(
            @PathVariable UUID supplierId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrdersBySupplierId(supplierId,pageable));
    }

    // 수령 업체별 주문 목록 조회
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<OrderResponseDto.OrderDetailResponse> getOrdersByReceiverId(
            @PathVariable UUID receiverId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getOrdersByReceiverId(receiverId,pageable));
    }

    // 주문 수정
    @PatchMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto.OrderDetailResponse> updateOrders(
            @PathVariable UUID orderId, OrderRequestDto.UpdateOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.updateOrder(orderId, request));
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    public ResponseEntity<void> deleteOrder(
            @PathVariable UUID orderId,
            @RequestHeader("X-User-Id") UUID deletedBy
            // 게이트웨이에ㅓ 헤더로 넘겨주는 유저 ID
    ) {
        orderService.deleteOrder(orderId, deletedBy);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


}
