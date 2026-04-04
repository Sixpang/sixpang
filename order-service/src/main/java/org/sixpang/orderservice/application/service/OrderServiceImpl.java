package org.sixpang.orderservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.model.entity.OrderItem;
import org.sixpang.orderservice.domain.repository.OrderItemRepository;
import org.sixpang.orderservice.domain.repository.OrderRepository;
import org.sixpang.orderservice.presentation.dto.OrderRequestDto;
import org.sixpang.orderservice.presentation.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 (조회 성능 최적화)
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 주문 생성
    @Override
    @Transactional // 쓰기 작업
    public OrderResponseDto.OrderDetailResponse createOrder(OrderRequestDto.CreateOrderRequest request){
        // total_price 계산
        // request에서 아이템 목록 꺼내서 (가격 * 수량) 합산
        // 지금은 가격이 없으니 추후 상품 서비스 연동 시 채울 예정
        BigDecimal totalPrice = BigDecimal.ZERO;

        // Order 엔티티 생성 후 저장
        Order order = Order.create(request, totalPrice);
        orderRepository.save(order);

        // OrderItem 엔티티 생성 후 저장
        List<OrderItem> orderItems = request.getOrderItems().stream()
                .map(item -> OrderItem.create(order.getId(), item))
                .toList();
        orderItems.forEach(orderItemRepository::save);

        // from()으로 엔티티 -> 레파지토리 변환 후 반환
        return OrderResponseDto.OrderDetailResponse.from(order, orderItems);
    }

    // 주문 단건 조회
    @Override
    public OrderResponseDto.OrderDetailResponse getOrder(UUID orderId) {

        // 주문 조회 (삭제 제외)
        // 없으면 예외 -> 공용 예외로 코드리뷰 이후 바꿀 예정
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 해당 주문의 아이템 목록 조회
        List<OrderItem> orderItems = orderItemRepository
                .findByOrderIdAndDeletedAtIsNull(orderId);

        // from()으로 변환 후 반환
        return OrderResponseDto.OrderDetailResponse.from(order, orderItems);
    }

    // 주문 전체 목록 조회 (MASTER)
    @Override
    public Page<OrderResponseDto.OrderResponse> getOrders(Pageable pageable) {

        // Page<ORder> 를 PAge<OrderResponse>로 변환
        return orderRepository.findAllByDeletedAtIsNull(pageable)
                .map(OrderResponseDto.OrderResponse::from);
    }

    // 공급 업체별 주문 목록 조회
    @Override
    public Page<OrderResponseDto.OrderResponse> getOrdersBySupplierId(
            UUID supplierId, Pageable pageable) {
        return orderRepository.findBySupplierIdAndDeletedAtIsNull(supplierId, pageable)
                .map(OrderResponseDto.OrderResponse::from);
    }

    // 수령 업체별 주문 목록 조회
    @Override
    public Page<OrderResponseDto.OrderResponse> getOrdersByReceiverId(
            UUID receiverId, Pageable pageable
    ) {
        return orderRepository.findByReceiverIdAndDeletedAtIsNull(receiverId, pageable)
                .map(OrderResponseDto.OrderResponse::from);
    }

    // 주문 수정
    @Override
    @Transactional
    public OrderResponseDto.OrderDetailResponse updateOrder(
            UUID orderId, OrderRequestDto.UpdateOrderRequest request) {

        // 주문 조회
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 엔티티 안의 update() 메서드 호출
        // 트랜잭션 이라 save() 없어도 변경사항 자동 반영 (변경 감지)
        order.update(request);

        // 아이템 조회 후 반환
        List<OrderItem> orderItems = orderItemRepository
                .findByOrderIdAndDeletedAtIsNull(orderId);

        return OrderResponseDto.OrderDetailResponse.from(order, orderItems);
    }

    // 주문 삭제
    @Override
    @Transactional
    public void deleteOrder(UUID orderId, UUID deletedBy) {

        // 주문 조회
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        // 베이스엔티티 softdelete 호출


        // 아이템도 같이 softDelete

    }
}
