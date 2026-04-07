package org.sixpang.orderservice.presentation.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.sixpang.orderservice.presentation.dto.CreateOrderRequest;
import org.sixpang.orderservice.presentation.dto.OrderItemRequest;
import org.sixpang.orderservice.presentation.dto.UpdateOrderRequest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * HttpClient 기반 실제 API 요청/응답 확인 테스트
 * <p>
 * - @SpringBootTest(webEnvironment = RANDOM_PORT) → 실제 서버 띄움
 * - Java 11+ java.net.http.HttpClient 사용
 * - JWT 토큰이 필요한 경우 아래 TOKEN 상수를 교체하거나
 * setUp()에서 로그인 API를 호출해 토큰을 동적으로 발급받으세요.
 * <p>
 * [주의]
 * - 이 테스트는 실제 DB / 외부 서비스(ProductClient 등)에 연결합니다.
 * - CI 환경에서는 @Disabled 처리하거나 별도 profile(test-integration)로 분리하세요.
 * - ProductClient는 WireMock으로 대체하는 것을 권장합니다. (아래 주석 참고)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderApiHttpClientTest {

    @LocalServerPort
    private int port;

    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private String baseUrl;

    // ──────────────────────────────────────────────────────────────
    // JWT 토큰 (로컬 실행 시 실제 토큰으로 교체)
    // 동적 발급이 필요하면 아래 loginAndGetToken() 참고
    // ──────────────────────────────────────────────────────────────
    private static final String MASTER_TOKEN = "Bearer <YOUR_MASTER_JWT_TOKEN>";
    private static final String NORMAL_TOKEN = "Bearer <YOUR_NORMAL_JWT_TOKEN>";

    // 테스트 간 공유 상태
    private static UUID createdOrderId;
    private static UUID testSupplierId = UUID.randomUUID();
    private static UUID testReceiverId = UUID.randomUUID();
    private static UUID testProductId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // LocalDateTime, Timestamp 직렬화 지원
    }

    // ================================================================
    // 1. 주문 생성 POST /api/orders
    // ================================================================
    @Test
    @org.junit.jupiter.api.Order(1)
    @DisplayName("[HttpClient] POST /api/orders → 주문 생성 성공")
    void test1_createOrder() throws Exception {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                testSupplierId,
                testReceiverId,
                Timestamp.from(Instant.now().plusSeconds(86400)),
                List.of(new OrderItemRequest(testProductId, 2))
        );
        String body = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/orders"))
                .header("Content-Type", "application/json")
                .header("Authorization", MASTER_TOKEN)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(10))
                .build();

        // when
        HttpResponse<String> response = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        // then
        System.out.println("▶ 상태 코드 : " + response.statusCode());
        System.out.println("▶ 응답 본문 : " + prettyPrint(response.body()));

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode root = objectMapper.readTree(response.body());
        assertThat(root.path("message").asText()).isEqualTo("주문 생성 성공");
        assertThat(root.path("data").path("orderStatus").asText()).isEqualTo("CONFIRMED");

        // 이후 테스트에서 사용할 orderId 저장
        createdOrderId = UUID.fromString(root.path("data").path("id").asText());
        System.out.println("▶ 생성된 orderId : " + createdOrderId);
    }

    // ================================================================
    // 2. 주문 단건 조회 GET /api/orders/{orderId}
    // ================================================================
    @Test
    @org.junit.jupiter.api.Order(2)
    @DisplayName("[HttpClient] GET /api/orders/{orderId} → 주문 단건 조회 성공")
    void test2_getOrder() throws Exception {
        Assumptions.assumeTrue(createdOrderId != null, "주문 생성 테스트가 선행되어야 합니다.");

        // given
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/orders/" + createdOrderId))
                .header("Authorization", MASTER_TOKEN)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        // when
        HttpResponse<String> response = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        // then
        System.out.println("▶ 상태 코드 : " + response.statusCode());
        System.out.println("▶ 응답 본문 : " + prettyPrint(response.body()));

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode root = objectMapper.readTree(response.body());
        assertThat(root.path("data").path("id").asText()).isEqualTo(createdOrderId.toString());
        assertThat(root.path("data").path("orderItems").isArray()).isTrue();
    }

    // ================================================================
    // 3. 존재하지 않는 주문 조회 → ORDER_NOT_FOUND 확인
    // ================================================================
    @Test
    @org.junit.jupiter.api.Order(3)
    @DisplayName("[HttpClient] GET /api/orders/{존재하지않는ID} → 404 에러 응답 확인")
    void test3_getOrder_notFound() throws Exception {
        // given
        UUID randomId = UUID.randomUUID();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/orders/" + randomId))
                .header("Authorization", MASTER_TOKEN)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        // when
        HttpResponse<String> response = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        // then
        System.out.println("▶ 상태 코드 : " + response.statusCode());
        System.out.println("▶ 응답 본문 : " + prettyPrint(response.body()));

        // GlobalExceptionHandler 응답 코드에 맞게 수정 (일반적으로 404 또는 400)
        assertThat(response.statusCode()).isIn(400, 404);
    }

    // ================================================================
    // 4. 전체 주문 목록 조회 GET /api/orders (MASTER only)
    // ================================================================
    @Test
    @org.junit.jupiter.api.Order(4)
    @DisplayName("[HttpClient] GET /api/orders?page=0&size=10 → 페이지 응답 확인")
    void test4_getOrders() throws Exception {
        // given
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/orders?page=0&size=10"))
                .header("Authorization", MASTER_TOKEN)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        // when
        HttpResponse<String> response = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        // then
        System.out.println("▶ 상태 코드 : " + response.statusCode());
        System.out.println("▶ 응답 본문 : " + prettyPrint(response.body()));

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode root = objectMapper.readTree(response.body());
        assertThat(root.path("data").path("content").isArray()).isTrue();
        System.out.println("▶ 전체 주문 수 : " + root.path("data").path("totalElements").asInt());
    }

    // ================================================================
    // 5. 주문 수정 PATCH /api/orders/{orderId}
    // ================================================================
    @Test
    @org.junit.jupiter.api.Order(5)
    @DisplayName("[HttpClient] PATCH /api/orders/{orderId} → 주문 수정 성공")
    void test5_updateOrder() throws Exception {
        Assumptions.assumeTrue(createdOrderId != null, "주문 생성 테스트가 선행되어야 합니다.");

        // given
        UpdateOrderRequest updateRequest = new UpdateOrderRequest(
                Timestamp.from(Instant.now().plusSeconds(172800)) // 2일 후
        );
        String body = objectMapper.writeValueAsString(updateRequest);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/orders/" + createdOrderId))
                .header("Content-Type", "application/json")
                .header("Authorization", MASTER_TOKEN)
                .method("PATCH", HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(10))
                .build();

        // when
        HttpResponse<String> response = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        // then
        System.out.println("▶ 상태 코드 : " + response.statusCode());
        System.out.println("▶ 응답 본문 : " + prettyPrint(response.body()));

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode root = objectMapper.readTree(response.body());
        assertThat(root.path("message").asText()).isEqualTo("주문 수정 성공");
    }

    // ================================================================
    // 6. 권한 없는 요청 → 403 확인
    // ================================================================
    @Test
    @org.junit.jupiter.api.Order(6)
    @DisplayName("[HttpClient] GET /api/orders (MASTER only) → 일반 유저 403 확인")
    void test6_getOrders_forbidden() throws Exception {
        // given
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/orders?page=0&size=10"))
                .header("Authorization", NORMAL_TOKEN)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        // when
        HttpResponse<String> response = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        // then
        System.out.println("▶ 상태 코드 : " + response.statusCode());
        System.out.println("▶ 응답 본문 : " + prettyPrint(response.body()));

        assertThat(response.statusCode()).isEqualTo(403);
    }

    // ================================================================
    // 7. 주문 삭제 DELETE /api/orders/{orderId}
    // ================================================================
    @Test
    @org.junit.jupiter.api.Order(7)
    @DisplayName("[HttpClient] DELETE /api/orders/{orderId} → 주문 삭제 성공")
    void test7_deleteOrder() throws Exception {
        Assumptions.assumeTrue(createdOrderId != null, "주문 생성 테스트가 선행되어야 합니다.");

        // given
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/orders/" + createdOrderId))
                .header("Authorization", MASTER_TOKEN)
                .DELETE()
                .timeout(Duration.ofSeconds(10))
                .build();

        // when
        HttpResponse<String> response = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        // then
        System.out.println("▶ 상태 코드 : " + response.statusCode());
        System.out.println("▶ 응답 본문 : " + prettyPrint(response.body()));

        assertThat(response.statusCode()).isEqualTo(200);

        JsonNode root = objectMapper.readTree(response.body());
        assertThat(root.path("message").asText()).isEqualTo("주문 삭제 성공");
    }

    // ================================================================
    // 8. 삭제된 주문 재조회 → 404 확인
    // ================================================================
    @Test
    @org.junit.jupiter.api.Order(8)
    @DisplayName("[HttpClient] 삭제된 주문 재조회 → 에러 응답 확인")
    void test8_getDeletedOrder_notFound() throws Exception {
        Assumptions.assumeTrue(createdOrderId != null, "주문 생성 테스트가 선행되어야 합니다.");

        // given
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/orders/" + createdOrderId))
                .header("Authorization", MASTER_TOKEN)
                .GET()
                .timeout(Duration.ofSeconds(10))
                .build();

        // when
        HttpResponse<String> response = httpClient.send(
                httpRequest, HttpResponse.BodyHandlers.ofString()
        );

        // then
        System.out.println("▶ 상태 코드 : " + response.statusCode());
        System.out.println("▶ 응답 본문 : " + prettyPrint(response.body()));

        assertThat(response.statusCode()).isIn(400, 404);
    }

    // ────────────────────────────────────────────────────────────────
    // 헬퍼
    // ────────────────────────────────────────────────────────────────

    /**
     * JSON 문자열을 보기 좋게 출력
     */
    private String prettyPrint(String json) {
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json; // JSON 파싱 실패 시 원본 반환
        }
    }

    /**
     * 로그인 API가 있는 경우 토큰을 동적으로 발급받는 예시
     *
     * private String loginAndGetToken(String username, String password) throws Exception {
     *     String loginBody = """
     *             { "username": "%s", "password": "%s" }
     *             """.formatted(username, password);
     *
     *     HttpRequest loginRequest = HttpRequest.newBuilder()
     *             .uri(URI.create(baseUrl + "/api/auth/login"))
     *             .header("Content-Type", "application/json")
     *             .POST(HttpRequest.BodyPublishers.ofString(loginBody))
     *             .build();
     *
     *     HttpResponse<String> response = httpClient.send(
     *             loginRequest, HttpResponse.BodyHandlers.ofString()
     *     );
     *     JsonNode root = objectMapper.readTree(response.body());
     *     return "Bearer " + root.path("data").path("token").asText();
     * }
     */
}