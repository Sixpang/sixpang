package org.sixpang.orderservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.model.entity.OrderItem;
import org.sixpang.orderservice.domain.repository.OrderItemRepository;
import org.sixpang.orderservice.domain.repository.OrderRepository;
import org.sixpang.orderservice.exception.OrderErrorCode;
import org.sixpang.orderservice.exception.OrderException;
import org.sixpang.orderservice.infrastructure.client.ProductClient;
import org.sixpang.orderservice.infrastructure.client.ProductStockResponse;
import org.sixpang.orderservice.presentation.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 (조회 성능 최적화)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductClient productClient; // FeignClient

    @Override
    @Transactional // 쓰기 작업
    public OrderDetailResponse createOrder(CreateOrderRequest request) {

        // total_price 계산
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        // 재고 확인 + totalPrice 계산
        for (OrderItemRequest itemRequest : request.getOrderItems()) {

            // 수량 검증
            if (itemRequest.getCount() < 1) {
                throw new CustomException(OrderErrorCode.ORDER_INVALID_QUANTITY);
            }

            // 상품 서비스에서 재고 확인 (FeignClient)
            ProductStockResponse product;
            try {
                product = productClient.getProductStock(itemRequest.getProductId());
            } catch (Exception e) {
                throw new CustomException(OrderErrorCode.PRODUCT_SERVICE_ERROR);
            }

            // 상품 없으면 예외
            if (product == null) {
                throw new CustomException(OrderErrorCode.PRODUCT_NOT_FOUND);
            }

            // 재고 부족하면 예외
            if (product.getStock() < itemRequest.getCount()) {
                throw new CustomException(OrderErrorCode.PRODUCT_OUT_OF_STOCK);
            }

            // totalPrice 합산
            BigDecimal itemTotal = product.getProductPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getCount()));
            totalPrice = totalPrice.add(itemTotal);

            // OrderItem 생성
            orderItems.add(OrderItem.create(
                    null, // orderId는 Order 저장 후 채움
                    itemRequest.getProductId(),
                    itemRequest.getCount()
            ));
        }

        // Order 생성 및 저장
        Order order = Order.create(
                request.getSupplierId(),
                request.getReceiverId(),
                request.getDeadlineAt(),
                totalPrice
        );
        orderRepository.save(order);


        List<OrderItem> savedItems = request.getOrderItems().stream()
                .map(item -> {
                    // 재고 확인 때 이미 검증했으므로 바로 생성
                    ProductStockResponse product = productClient.getProductStock(item.getProductId());
                    return OrderItem.create(
                            order.getId(),
                            item.getProductId(),
                            item.getCount()
                    );
                })
                .toList();
        savedItems.forEach(orderItemRepository::save);

        return OrderDetailResponse.fromEntity(order, savedItems);
    }

    // 주문 전체 목록 조회 (MASTER)
    public OrderPageResponse getOrders(Pageable pageable) {

        return new OrderPageResponse(
                orderRepository.findAllByDeletedAtIsNull(pageable)
        );
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
    public OrderPageResponse getOrdersBySupplierId(
            UUID supplierId, Pageable pageable
    ) {
        return new OrderPageResponse(
                orderRepository.findBySupplierIdAndDeletedAtIsNull(supplierId, pageable)
        );
    }


    @Override
    public OrderPageResponse getOrdersByReceiverId(
            UUID receiverId, Pageable pageable
    ) {
        return new OrderPageResponse(
                orderRepository.findByReceiverIdAndDeletedAtIsNull(receiverId, pageable)
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
