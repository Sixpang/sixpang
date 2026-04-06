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
import org.sixpang.orderservice.infrastructure.client.dto.InventoryResponse;
import org.sixpang.orderservice.infrastructure.client.dto.UpdateInventoryRequest;
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

        BigDecimal totalPrice = BigDecimal.ZERO;

        // 재고 확인
        for (OrderItemRequest item : request.getOrderItems()) {
            InventoryResponse inventory;
            try {
                inventory = productClient.getInventory(item.getProductId());
            } catch (Exception e) {
                throw new CustomException(OrderErrorCode.PRODUCT_SERVICE_ERROR);
            }

            // record는 .quantity() 로 접근
            if (inventory.quantity() < item.getCount()) {
                throw new CustomException(OrderErrorCode.PRODUCT_OUT_OF_STOCK);
            }
        }

        // Order 생성 및 저장
        Order order = Order.create(
                request.getSupplierId(),
                request.getReceiverId(),
                request.getDeadlineAt(),
                totalPrice
        );
        orderRepository.save(order);

        // 3. OrderItem 생성 + 재고 감소
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest item : request.getOrderItems()) {
            try {
                productClient.decreaseInventory(
                        item.getProductId(),
                        new UpdateInventoryRequest(item.getCount())
                );
            } catch (Exception e) {
                throw new CustomException(OrderErrorCode.PRODUCT_SERVICE_ERROR);
            }

            orderItems.add(OrderItem.create(
                    order.getId(),
                    item.getProductId(),
                    item.getCount()
            ));
        }
        orderItems.forEach(orderItemRepository::save);

        return OrderDetailResponse.fromEntity(order, orderItems);
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
