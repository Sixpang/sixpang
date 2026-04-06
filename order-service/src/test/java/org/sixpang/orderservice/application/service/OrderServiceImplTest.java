package org.sixpang.orderservice.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;
import org.sixpang.orderservice.domain.repository.OrderRepository;
import scala.Product;

class OrderServiceImplTest {

    @InjectMocks
    private OrderService orderService; // 테스트 대상

    @Mock
    private OrderRepository orderRepository; // 가짜 저장소

    @Mock
    private ProductService productService; // 가짜 상품 서비스 (재고 확인용)

    @Test
    @DisplayName("주문 성공: 재고가 충분하면 주문이 생성되고 상태가 'ORDERED'가 된다")
    void createOrder_Success() {
        // [Given] 테스트를 위한 상황 설정
        Long productId = 1L;
        int orderQuantity = 2;
        OrderRequest request = new OrderRequest(productId, orderQuantity);

        // 가짜 상품 정보 (재고 10개, 가격 10,000원)
        Product mockProduct = new Product(productId, "테스트 노트북", 10000, 10);

        // Mock 동작 정의: 상품 조회 시 mockProduct를 반환하고, 저장 시 입력받은 객체를 그대로 반환함
        given(productService.getProduct(productId)).willReturn(mockProduct);
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> invocation.getArgument(0));

        // [When] 실제 테스트할 로직 실행
        Order result = orderService.createOrder(request);

        // [Then] 결과 검증
        assertAll(
                // 1. 주문 상태가 정상적으로 ORDERED인지 확인
                () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.ORDERED),

                // 2. 총 금액 계산이 맞는지 확인 (10,000 * 2 = 20,000)
                () -> assertThat(result.getTotalPrice()).isEqualTo(20000),

                // 3. 내부적으로 재고 차감 메서드가 실제 호출되었는지 확인
                () -> verify(productService, times(1)).deductStock(productId, orderQuantity)
        );
    }

    @Test
    @DisplayName("주문 실패: 재고보다 많은 수량을 주문하면 예외가 발생한다")
    void createOrder_Fail_OutOfStock() {
        // [Given] 재고는 5개인데 10개를 주문하는 상황
        Long productId = 1L;
        Product mockProduct = new Product(productId, "테스트 노트북", 10000, 5);
        OrderRequest request = new OrderRequest(productId, 10);

        given(productService.getProduct(productId)).willReturn(mockProduct);

        // [When & Then] 예외가 발생하는지 검증
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(OutOfStockException.class)
                .hasMessageContaining("재고가 부족합니다");

        // 저장 로직은 절대 호출되면 안 됨
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrders() {
    }

    @Test
    void getOrdersBySupplierId() {
    }

    @Test
    void getOrdersByReceiverId() {
    }

    @Test
    void updateOrder() {
    }

    @Test
    void deleteOrder() {
    }
}