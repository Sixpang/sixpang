package org.sixpang.orderservice.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.response.PageResponse;
import org.sixpang.orderservice.application.service.OrderService;
import org.sixpang.orderservice.domain.model.enums.OrderStatus;
import org.sixpang.orderservice.exception.OrderErrorCode;
import org.sixpang.orderservice.presentation.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * OrderController 통합 테스트
 * <p>
 * - @WebMvcTest: Controller 레이어만 로드 (Service, Repository 는 Mock 처리)
 * - Spring Security 가 활성화된 경우 @WithMockUser 또는 커스텀 SecurityConfig 비활성화 필요
 * <p>
 * ※ 프로젝트의 Security 설정에 따라 아래 두 가지 중 하나를 선택하세요:
 * [방법 A] @WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)  → Security 완전 제거
 * [방법 B] @WithMockUser(roles = "MASTER")  → Spring Security 유지, 테스트 유저 주입
 * <p>
 * 아래 예제는 [방법 B] 기준으로 작성되었습니다.
 */

// Security 설정에 따라 둘 중 하나 선택
// [A] Security 완전 제거
// [B] 현재 코드 방식 - MockMvcRequestPostProcessors.user() 사용
// @PreAuthorize("hasRole('MASTER')") 통과시키려면 roles = "MASTER" 필요
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    // ────────────────────────────────────────────────────────────────
    // 공통 픽스처
    // ────────────────────────────────────────────────────────────────

    private UUID orderId;
    private UUID supplierId;
    private UUID receiverId;
    private UUID productId;

    private OrderDetailResponse mockDetailResponse;
    private CreateOrderRequest createRequest;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        supplierId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        productId = UUID.randomUUID();

        OrderItemResponse itemResponse = new OrderItemResponse(
                UUID.randomUUID(), orderId, productId, "테스트 상품", BigDecimal.valueOf(1000), 3
        );

        mockDetailResponse = new OrderDetailResponse(
                orderId,
                supplierId,
                receiverId,
                OrderStatus.CONFIRMED,
                BigDecimal.ZERO,
                Timestamp.from(Instant.now().plusSeconds(86400)),
                LocalDateTime.now(),
                List.of(itemResponse)
        );

        createRequest = new CreateOrderRequest(
                supplierId,
                receiverId,
                Timestamp.from(Instant.now().plusSeconds(86400)),
                List.of(new OrderItemRequest(productId, 3))
        );
    }

    // ================================================================
    // POST /api/orders  (주문 생성)
    // ================================================================
    @Nested
    @DisplayName("POST /api/orders - 주문 생성")
    class CreateOrderApi {

        @Test
        @DisplayName("정상 요청 → 200 OK + 주문 상세 응답")
        void createOrder_success() throws Exception {
            // given
            given(orderService.createOrder(any(CreateOrderRequest.class)))
                    .willReturn(mockDetailResponse);

            // when
            ResultActions result = mockMvc.perform(
                    post("/api/orders")
                            .with(csrf())
                            .with(mockMasterUser())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("주문 생성 성공"))
                    .andExpect(jsonPath("$.data.supplierId").value(supplierId.toString()))
                    .andExpect(jsonPath("$.data.receiverId").value(receiverId.toString()))
                    .andExpect(jsonPath("$.data.orderStatus").value("CONFIRMED"))
                    .andExpect(jsonPath("$.data.orderItems").isArray())
                    .andExpect(jsonPath("$.data.orderItems[0].count").value(3));
        }

        @Test
        @DisplayName("supplierId 누락 → 400 Bad Request (Validation 실패)")
        void createOrder_validationFail_missingSupplierId() throws Exception {
            // given: supplierId = null
            CreateOrderRequest invalidRequest = new CreateOrderRequest(
                    null,
                    receiverId,
                    Timestamp.from(Instant.now().plusSeconds(86400)),
                    List.of(new OrderItemRequest(productId, 3))
            );

            // when & then
            mockMvc.perform(
                            post("/api/orders")
                                    .with(csrf())
                                    .with(mockMasterUser())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("재고 부족 → 적절한 에러 응답")
        void createOrder_outOfStock() throws Exception {
            // given
            given(orderService.createOrder(any(CreateOrderRequest.class)))
                    .willThrow(new CustomException(OrderErrorCode.PRODUCT_OUT_OF_STOCK));

            // when & then
            mockMvc.perform(
                            post("/api/orders")
                                    .with(csrf())
                                    .with(mockMasterUser())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(createRequest))
                    )
                    .andDo(print())
                    // GlobalExceptionHandler 의 응답 코드에 맞게 수정하세요 (예: 400, 409 등)
                    .andExpect(status().is4xxClientError());
        }
    }

    // ================================================================
    // GET /api/orders/{orderId}  (주문 단건 조회)
    // ================================================================
    @Nested
    @DisplayName("GET /api/orders/{orderId} - 주문 단건 조회")
    class GetOrderApi {

        @Test
        @DisplayName("존재하는 주문 → 200 OK + 상세 응답")
        void getOrder_success() throws Exception {
            // given
            given(orderService.getOrder(eq(orderId), any(UserPrincipal.class)))
                    .willReturn(mockDetailResponse);

            // when & then
            mockMvc.perform(
                            get("/api/orders/{orderId}", orderId)
                                    .with(mockMasterUser())
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("주문 단건 조회 성공"))
                    .andExpect(jsonPath("$.data.id").value(orderId.toString()));
        }

        @Test
        @DisplayName("존재하지 않는 주문 → 404 에러 응답")
        void getOrder_notFound() throws Exception {
            // given
            given(orderService.getOrder(eq(orderId), any(UserPrincipal.class)))
                    .willThrow(new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

            // when & then
            mockMvc.perform(
                            get("/api/orders/{orderId}", orderId)
                                    .with(mockMasterUser())
                    )
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("접근 권한 없음 → 403 에러 응답")
        void getOrder_accessDenied() throws Exception {
            // given
            given(orderService.getOrder(eq(orderId), any(UserPrincipal.class)))
                    .willThrow(new CustomException(OrderErrorCode.ORDER_ACCESS_DENIED));

            // when & then
            mockMvc.perform(
                            get("/api/orders/{orderId}", orderId)
                                    .with(mockNormalUser())
                    )
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }

    // ================================================================
    // GET /api/orders  (전체 목록 조회 - MASTER only)
    // ================================================================
    @Nested
    @DisplayName("GET /api/orders - 전체 목록 조회")
    class GetOrdersApi {

        @Test
        @DisplayName("MASTER 권한 → 200 OK + 페이지 응답")
        void getOrders_masterSuccess() throws Exception {
            // given
            PageResponse<OrderResponse> pageResponse = PageResponse.empty(); // 빈 페이지
            given(orderService.getOrders(any(Pageable.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            get("/api/orders")
                                    .with(mockMasterUser())
                                    .param("page", "0")
                                    .param("size", "10")
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("주문 전체 목록 조회 성공"));
        }

        @Test
        @DisplayName("일반 유저 → @PreAuthorize 거부 → 403")
        void getOrders_forbidden() throws Exception {
            mockMvc.perform(
                            get("/api/orders")
                                    .with(mockNormalUser())
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    // ================================================================
    // PATCH /api/orders/{orderId}  (주문 수정)
    // ================================================================
    @Nested
    @DisplayName("PATCH /api/orders/{orderId} - 주문 수정")
    class UpdateOrderApi {

        @Test
        @DisplayName("정상 수정 → 200 OK")
        void updateOrder_success() throws Exception {
            // given
            UpdateOrderRequest updateRequest = new UpdateOrderRequest(
                    Timestamp.from(Instant.now().plusSeconds(172800))
            );
            given(orderService.updateOrder(eq(orderId), any(UpdateOrderRequest.class), any(UserPrincipal.class)))
                    .willReturn(mockDetailResponse);

            // when & then
            mockMvc.perform(
                            patch("/api/orders/{orderId}", orderId)
                                    .with(csrf())
                                    .with(mockMasterUser())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateRequest))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("주문 수정 성공"));
        }

        @Test
        @DisplayName("CANCELLED 상태 주문 수정 → 에러 응답")
        void updateOrder_cannotUpdate() throws Exception {
            // given
            UpdateOrderRequest updateRequest = new UpdateOrderRequest(
                    Timestamp.from(Instant.now().plusSeconds(172800))
            );
            given(orderService.updateOrder(eq(orderId), any(), any()))
                    .willThrow(new CustomException(OrderErrorCode.ORDER_CANNOT_UPDATE));

            // when & then
            mockMvc.perform(
                            patch("/api/orders/{orderId}", orderId)
                                    .with(csrf())
                                    .with(mockMasterUser())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateRequest))
                    )
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }

    // ================================================================
    // DELETE /api/orders/{orderId}  (주문 삭제)
    // ================================================================
    @Nested
    @DisplayName("DELETE /api/orders/{orderId} - 주문 삭제")
    class DeleteOrderApi {

        @Test
        @DisplayName("정상 삭제 → 200 OK")
        void deleteOrder_success() throws Exception {
            // given
            willDoNothing().given(orderService).deleteOrder(eq(orderId), any(UserPrincipal.class));

            // when & then
            mockMvc.perform(
                            delete("/api/orders/{orderId}", orderId)
                                    .with(csrf())
                                    .with(mockMasterUser())
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("주문 삭제 성공"));
        }

        @Test
        @DisplayName("존재하지 않는 주문 삭제 → 에러 응답")
        void deleteOrder_notFound() throws Exception {
            // given
            willThrow(new CustomException(OrderErrorCode.ORDER_NOT_FOUND))
                    .given(orderService).deleteOrder(eq(orderId), any(UserPrincipal.class));

            // when & then
            mockMvc.perform(
                            delete("/api/orders/{orderId}", orderId)
                                    .with(csrf())
                                    .with(mockMasterUser())
                    )
                    .andDo(print())
                    .andExpect(status().is4xxClientError());
        }
    }

    // ────────────────────────────────────────────────────────────────
    // 헬퍼: UserPrincipal을 Spring Security Context에 주입
    // ────────────────────────────────────────────────────────────────

    /**
     * MASTER 권한 Mock 유저
     * - @PreAuthorize("hasRole('MASTER')") 를 통과시키려면 roles = "MASTER" 필요
     */
    private SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor mockMasterUser() {
        return SecurityMockMvcRequestPostProcessors.user("master-user")
                .roles("MASTER")
                .password("password");
    }

    /**
     * 일반(HUB_MANAGER) 권한 Mock 유저
     */
    private SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor mockNormalUser() {
        return SecurityMockMvcRequestPostProcessors.user("normal-user")
                .roles("HUB_MANAGER")
                .password("password");
    }
}