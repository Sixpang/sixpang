package org.sixpang.orderservice.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sixpang.commonserver.global.CustomException;
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
import org.sixpang.orderservice.presentation.dto.CreateOrderRequest;
import org.sixpang.orderservice.presentation.dto.OrderDetailResponse;
import org.sixpang.orderservice.presentation.dto.OrderItemRequest;
import org.sixpang.orderservice.presentation.dto.UpdateOrderRequest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private OrderEventPublisher eventPublisher;

    // ────────────────────────────────────────────────────────────────
    // 공통 픽스처
    // ────────────────────────────────────────────────────────────────

    private UUID orderId;
    private UUID supplierId;
    private UUID receiverId;
    private UUID productId;
    private UUID userId;

    private Order mockOrder;
    private OrderItem mockOrderItem;
    private CreateOrderRequest createRequest;
    private UserPrincipal masterUser;
    private UserPrincipal normalUser;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        supplierId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        productId = UUID.randomUUID();
        userId = UUID.randomUUID();

        // 헬퍼를 사용하여 상태는 CONFIRMED, 작성자는 userId로 세팅
        mockOrder = createOrderWithMetadata(OrderStatus.CONFIRMED, userId);

        mockOrderItem = OrderItem.create(orderId, productId, 5);

        OrderItemRequest itemRequest = new OrderItemRequest(productId, 3);
        createRequest = new CreateOrderRequest(
                supplierId,
                receiverId,
                Timestamp.from(Instant.now().plusSeconds(86400)),
                List.of(itemRequest)
        );

        /// 유저 권한 설정
        masterUser = new UserPrincipal(userId, "MASTER"); // 작성자와 동일 ID
        normalUser = new UserPrincipal(UUID.randomUUID(), "HUB_MANAGER"); // 작성자와 다른 ID
    }

    // ================================================================
    // createOrder
    // ================================================================
    @Nested
    @DisplayName("주문 생성 (createOrder)")
    class CreateOrder {

        @Test
        @DisplayName("정상 주문 생성 → OrderDetailResponse 반환 + 이벤트 발행")
        void createOrder_success() {
            // given
            given(productClient.getInventory(productId))
                    .willReturn(new InventoryResponse(productId, 100L));
            given(orderRepository.save(any(Order.class))).willReturn(mockOrder);
            given(orderItemRepository.save(any(OrderItem.class))).willReturn(mockOrderItem);
            willDoNothing().given(eventPublisher).publish(any(OrderCreatedEvent.class));

            // when
            OrderDetailResponse response = orderService.createOrder(createRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getSupplierId()).isEqualTo(supplierId);
            assertThat(response.getReceiverId()).isEqualTo(receiverId);
            assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.CONFIRMED);

            then(productClient).should(times(1)).getInventory(productId);
            then(productClient).should(times(1))
                    .decreaseInventory(eq(productId), any(UpdateInventoryRequest.class));
            then(eventPublisher).should(times(1)).publish(any(OrderCreatedEvent.class));
        }

        @Test
        @DisplayName("재고 부족 → PRODUCT_OUT_OF_STOCK 예외")
        void createOrder_outOfStock() {
            // given: 재고 1개, 요청 수량 3개
            given(productClient.getInventory(productId))
                    .willReturn(new InventoryResponse(productId, 1L));

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(createRequest))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.PRODUCT_OUT_OF_STOCK)
                    );

            then(orderRepository).should(never()).save(any());
            then(eventPublisher).should(never()).publish(any());
        }

        @Test
        @DisplayName("ProductClient 재고 조회 실패 → PRODUCT_SERVICE_ERROR 예외")
        void createOrder_productClientError() {
            // given
            given(productClient.getInventory(productId))
                    .willThrow(new RuntimeException("Feign timeout"));

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(createRequest))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.PRODUCT_SERVICE_ERROR)
                    );
        }

        @Test
        @DisplayName("재고 감소 API 실패 → PRODUCT_SERVICE_ERROR 예외")
        void createOrder_decreaseInventoryFails() {
            // given
            given(productClient.getInventory(productId))
                    .willReturn(new InventoryResponse(productId, 100L));
            given(orderRepository.save(any(Order.class))).willReturn(mockOrder);
            willThrow(new RuntimeException("Feign error"))
                    .given(productClient)
                    .decreaseInventory(eq(productId), any(UpdateInventoryRequest.class));

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(createRequest))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.PRODUCT_SERVICE_ERROR)
                    );
        }
    }

    // ================================================================
    // getOrder
    // ================================================================
    @Nested
    @DisplayName("주문 단건 조회 (getOrder)")
    class GetOrder {

        @Test
        @DisplayName("MASTER 권한 → 타인 주문도 조회 가능")
        void getOrder_masterCanViewAny() {
            // given
            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(mockOrder));
            given(orderItemRepository.findByOrderIdAndDeletedAtIsNull(orderId))
                    .willReturn(List.of(mockOrderItem));

            // when
            OrderDetailResponse response = orderService.getOrder(orderId, masterUser);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getOrderItems()).hasSize(1);
        }

        @Test
        @DisplayName("일반 유저 → 타인 주문 조회 시 ORDER_ACCESS_DENIED")
        void getOrder_normalUserAccessDenied() {
            // given: mockOrder.createdBy ≠ normalUser.userId
            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(mockOrder));

            // when & then
            assertThatThrownBy(() -> orderService.getOrder(orderId, normalUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.ORDER_ACCESS_DENIED)
                    );
        }

        @Test
        @DisplayName("존재하지 않는 orderId → ORDER_NOT_FOUND")
        void getOrder_notFound() {
            // given
            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.getOrder(orderId, masterUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.ORDER_NOT_FOUND)
                    );
        }
    }

    // ================================================================
    // updateOrder
    // ================================================================
    @Nested
    @DisplayName("주문 수정 (updateOrder)")
    class UpdateOrder {

        private UpdateOrderRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = new UpdateOrderRequest(
                    Timestamp.from(Instant.now().plusSeconds(172800))
            );
        }

        @Test
        @DisplayName("CONFIRMED 상태 주문 → 정상 수정")
        void updateOrder_success() {
            // given: mockOrder 는 기본적으로 CONFIRMED 상태
            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(mockOrder));
            given(orderItemRepository.findByOrderIdAndDeletedAtIsNull(orderId))
                    .willReturn(List.of(mockOrderItem));

            // when
            OrderDetailResponse response = orderService.updateOrder(orderId, updateRequest, masterUser);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getDeadlineAt()).isEqualTo(updateRequest.getDeadlineAt());
        }

        @Test
        @DisplayName("CANCELLED 상태 주문 → ORDER_CANNOT_UPDATE 예외")
        void updateOrder_cancelledStatus() {
            // AS-IS: Order cancelledOrder = createOrderWithMetadata(OrderStatus.CANCELLED);
            // TO-BE: 두 번째 인자인 userId(또는 임의의 UUID)를 추가해야 합니다.
            Order cancelledOrder = createOrderWithMetadata(OrderStatus.CANCELLED, userId);

            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(cancelledOrder));

            // when & then
            assertThatThrownBy(() -> orderService.updateOrder(orderId, updateRequest, masterUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.ORDER_CANNOT_UPDATE)
                    );
        }

        @Test
        @DisplayName("일반 유저 → 타인 주문 수정 시 ORDER_ACCESS_DENIED")
        void updateOrder_accessDenied() {
            // given
            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(mockOrder));

            // when & then
            assertThatThrownBy(() -> orderService.updateOrder(orderId, updateRequest, normalUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.ORDER_ACCESS_DENIED)
                    );
        }
    }

    // ================================================================
    // deleteOrder
    // ================================================================
    @Nested
    @DisplayName("주문 삭제 (deleteOrder)")
    class DeleteOrder {

        @Test
        @DisplayName("MASTER → 정상 소프트 삭제")
        void deleteOrder_success() {
            // given
            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(mockOrder));
            given(orderItemRepository.findByOrderIdAndDeletedAtIsNull(orderId))
                    .willReturn(List.of(mockOrderItem));

            // when
            assertThatCode(() -> orderService.deleteOrder(orderId, masterUser))
                    .doesNotThrowAnyException();

            // then: delete() 호출 여부 확인은 Order 내부 상태로 검증
        }

        @Test
        @DisplayName("존재하지 않는 orderId → ORDER_NOT_FOUND")
        void deleteOrder_notFound() {
            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.deleteOrder(orderId, masterUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.ORDER_NOT_FOUND)
                    );
        }

        @Test
        @DisplayName("일반 유저 → 타인 주문 삭제 시 ORDER_ACCESS_DENIED")
        void deleteOrder_accessDenied() {
            given(orderRepository.findByIdAndDeletedAtIsNull(orderId))
                    .willReturn(Optional.of(mockOrder));

            assertThatThrownBy(() -> orderService.deleteOrder(orderId, normalUser))
                    .isInstanceOf(CustomException.class)
                    .satisfies(ex ->
                            assertThat(((CustomException) ex).getErrorCode())
                                    .isEqualTo(OrderErrorCode.ORDER_ACCESS_DENIED)
                    );
        }
    }

    // ────────────────────────────────────────────────────────────────
    // 헬퍼: 리플렉션으로 OrderStatus 강제 세팅
    // ────────────────────────────────────────────────────────────────
    private Order createOrderWithMetadata(OrderStatus status, UUID createdBy) {
        Order order = Order.create(
                supplierId, receiverId,
                Timestamp.from(Instant.now().plusSeconds(86400)),
                BigDecimal.ZERO
        );
        try {
            // 1. 주문 상태(orderStatus) 강제 주입
            var statusField = Order.class.getDeclaredField("orderStatus");
            statusField.setAccessible(true);
            statusField.set(order, status);

            // 2. 작성자(createdBy) 강제 주입 (BaseEntity 필드 접근)
            // getSuperclass()를 통해 BaseEntity에 선언된 필드를 가져옵니다.
            var createdByField = Order.class.getSuperclass().getDeclaredField("createdBy");
            createdByField.setAccessible(true);
            createdByField.set(order, createdBy);
        } catch (Exception e) {
            throw new RuntimeException("리플렉션 세팅 실패", e);
        }
        return order;
    }
}