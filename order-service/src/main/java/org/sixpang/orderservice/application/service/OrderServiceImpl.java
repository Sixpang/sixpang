package org.sixpang.orderservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.model.entity.OrderItem;
import org.sixpang.orderservice.domain.repository.OrderItemRepository;
import org.sixpang.orderservice.domain.repository.OrderRepository;
import org.sixpang.orderservice.exception.OrderErrorCode;
import org.sixpang.orderservice.exception.OrderException;
import org.sixpang.orderservice.presentation.dto.CreateOrderRequest;
import org.sixpang.orderservice.presentation.dto.OrderDetailResponse;
import org.sixpang.orderservice.presentation.dto.OrderResponse;
import org.sixpang.orderservice.presentation.dto.UpdateOrderRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 (조회 성능 최적화)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // 페이징 처리 - 허용 사이즈 10/30/50 외에는 기본 10
    private Pageable buildPageable(int page, int size, String sortBy, String sortDir) {
        if (size != 10 && size != 30 && size != 50) {
            size = 10;
        }
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    @Override
    @Transactional // 쓰기 작업
    public OrderDetailResponse createOrder(CreateOrderRequest request) {


        // total_price 계산
        // request에서 아이템 목록 꺼내서 (가격 * 수량) 합산
        // 지금은 가격이 없으니 추후 상품 서비스 연동 시 채울 예정
        BigDecimal totalPrice = BigDecimal.ZERO;

        Order order = Order.create(
                request.getSupplierId(),
                request.getReceiverId(),
                request.getDeadlineAt(),
                totalPrice
        );
        orderRepository.save(order);


        List<OrderItem> orderItems = request.getOrderItems().stream()
                .map(item -> OrderItem.create(
                        order.getId(),
                        item.getProductId(),
                        item.getCount()
                ))
                .toList();
        orderItems.forEach(orderItemRepository::save);

        return OrderDetailResponse.fromEntity(order, orderItems);
    }

    @Override
    public OrderDetailResponse getOrder(UUID orderId) {

        // 주문 조회 (삭제 제외)
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        // 해당 주문의 아이템 목록 조회
        List<OrderItem> orderItems = orderItemRepository
                .findByOrderIdAndDeletedAtIsNull(orderId);

        // from()으로 변환 후 반환
        return OrderDetailResponse.fromEntity(order, orderItems);
    }

    @Override
    public PageResponse<OrderResponse> getOrders(int page, int size, String sortBy, String sortDir) {
        return PageResponse.from(
                orderRepository.findAllByDeletedAtIsNull(buildPageable(page, size, sortBy, sortDir))
                        .map(OrderResponse::fromEntity)
        );
    }

    @Override
    public PageResponse<OrderResponse> getOrdersBySupplierId(UUID supplierId, int page, int size, String sortBy, String sortDir) {
        return PageResponse.from(
                orderRepository.findBySupplierIdAndDeletedAtIsNull(supplierId, buildPageable(page, size, sortBy, sortDir))
                        .map(OrderResponse::fromEntity)
        );
    }

    @Override
    public PageResponse<OrderResponse> getOrdersByReceiverId(UUID receiverId, int page, int size, String sortBy, String sortDir) {
        return PageResponse.from(
                orderRepository.findByReceiverIdAndDeletedAtIsNull(receiverId, buildPageable(page, size, sortBy, sortDir))
                        .map(OrderResponse::fromEntity)
        );
    }


    // 주문 수정
    @Override
    @Transactional
    public OrderDetailResponse updateOrder(
            UUID orderId, UpdateOrderRequest request
    ) {
        // 주문 조회
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        order.update(request.getDeadlineAt());

        // 아이템 조회 후 반환
        List<OrderItem> orderItems = orderItemRepository
                .findByOrderIdAndDeletedAtIsNull(orderId);

        return OrderDetailResponse.fromEntity(order, orderItems);
    }

    // 주문 삭제
    @Override
    @Transactional
    public void deleteOrder(UUID orderId, UUID deletedBy) {

        // 주문 조회
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new OrderException(OrderErrorCode.ORDER_NOT_FOUND));

        order.delete(deletedBy);

        List<OrderItem> orderItems = orderItemRepository
                .findByOrderIdAndDeletedAtIsNull(orderId);
        orderItems.forEach(item -> item.delete(deletedBy));
    }
}
