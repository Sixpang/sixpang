package org.sixpang.orderservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.orderservice.domain.repository.OrderItemRepository;
import org.sixpang.orderservice.domain.repository.OrderRepository;
import org.sixpang.orderservice.presentation.dto.OrderRequestDto;
import org.sixpang.orderservice.presentation.dto.OrderResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 (조회 성능 최적화)
public class OrderServiceImpl {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 주문 생성
    @Override
    @Transactional // 쓰기 작업
    public OrderResponseDto.OrderDetailResponse createOrder(OrderRequestDto.CreateOrderRequest request);

    // total_price 계산
    // request에서 아이템 목록 꺼내서 (가격 * 수량) 합산
    // 지금은 가격이 없으니 추후 상품 서비스 연동 시 채울 예정
    BigDecimal totalPrice = request.getOrderItems().stream()
}
