package org.sixpang.orderservice.application.service;

import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.commonserver.security.UserPrincipal;
import org.sixpang.orderservice.application.event.OrderCreatedEvent;
import org.sixpang.orderservice.application.event.OrderEventPublisher;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.sixpang.orderservice.domain.model.entity.OrderItem;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;
import org.sixpang.orderservice.domain.repository.OrderItemRepository;
import org.sixpang.orderservice.domain.repository.OrderRepository;
import org.sixpang.orderservice.exception.OrderErrorCode;
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
    private final ProductClient productClient;

    private final OrderEventPublisher eventPublisher;

    @Override
    @Transactional
    public OrderDetailResponse createOrder(CreateOrderRequest request) {

        BigDecimal totalPrice = BigDecimal.ZERO;

        // 1. 재고 확인
        for (OrderItemRequest item : request.getOrderItems()) {
            InventoryResponse inventory;
            try {
                inventory = productClient.getInventory(item.getProductId());
            } catch (Exception e) {
                throw new CustomException(OrderErrorCode.PRODUCT_SERVICE_ERROR);
            }
            if (inventory.quantity() < item.getCount()) {
                throw new CustomException(OrderErrorCode.PRODUCT_OUT_OF_STOCK);
            }
        }

        // 2. Order 생성 및 저장
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

        // 이벤트 발행 (이 부분이 있어야 Kafka로 메시지가 발송됩니다)
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getSupplierId(),
                order.getReceiverId()
        );

        eventPublisher.publish(event);

        return OrderDetailResponse.fromEntity(order, orderItems);
    }

    @Override
    public OrderDetailResponse getOrder(UUID orderId, UserPrincipal user) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        // MASTER가 아니면 본인 주문만 조회 가능
        if (!"MASTER".equals(user.getRole()) &&
                !order.getCreatedBy().equals(user.getUserId())) {
            throw new CustomException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }

        List<OrderItem> orderItems = orderItemRepository
                .findByOrderIdAndDeletedAtIsNull(orderId);

        return OrderDetailResponse.fromEntity(order, orderItems);
    }

    @Override
    public PageResponse<OrderResponse> getOrders(Pageable pageable) {
        return PageResponse.from(
                orderRepository.findAllByDeletedAtIsNull(pageable)
                        .map(OrderResponse::fromEntity)
        );
    }

    @Override
    public PageResponse<OrderResponse> getOrdersBySupplierId(UUID supplierId, Pageable pageable) {
        return PageResponse.from(
                orderRepository.findBySupplierIdAndDeletedAtIsNull(supplierId, pageable)
                        .map(OrderResponse::fromEntity)
        );
    }

    @Override
    public PageResponse<OrderResponse> getOrdersByReceiverId(UUID receiverId, Pageable pageable) {
        return PageResponse.from(
                orderRepository.findByReceiverIdAndDeletedAtIsNull(receiverId, pageable)
                        .map(OrderResponse::fromEntity)
        );
    }

    @Override
    @Transactional
    public OrderDetailResponse updateOrder(UUID orderId, UpdateOrderRequest request, UserPrincipal user) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        // MASTER가 아니면 본인 주문만 수정 가능
        if (!"MASTER".equals(user.getRole()) &&
                !order.getCreatedBy().equals(user.getUserId())) {
            throw new CustomException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }

        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new CustomException(OrderErrorCode.ORDER_CANNOT_UPDATE);
        }

        order.update(request.getDeadlineAt());

        List<OrderItem> orderItems = orderItemRepository
                .findByOrderIdAndDeletedAtIsNull(orderId);

        return OrderDetailResponse.fromEntity(order, orderItems);
    }

    @Override
    @Transactional
    public void deleteOrder(UUID orderId, UserPrincipal user) {
        Order order = orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        // MASTER가 아니면 본인 주문만 삭제 가능
        if (!"MASTER".equals(user.getRole()) &&
                !order.getCreatedBy().equals(user.getUserId())) {
            throw new CustomException(OrderErrorCode.ORDER_ACCESS_DENIED);
        }

        order.delete(user.getUserId());

        List<OrderItem> orderItems = orderItemRepository
                .findByOrderIdAndDeletedAtIsNull(orderId);
        orderItems.forEach(item -> item.delete(user.getUserId()));
    }
}
