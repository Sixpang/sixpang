package org.sixpang.orderservice.presentation.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

// Security 설정에 따라 둘 중 하나 선택
// [A] Security 완전 제거
// [B] 현재 코드 방식 - MockMvcRequestPostProcessors.user() 사용
// @PreAuthorize("hasRole('MASTER')") 통과시키려면 roles = "MASTER" 필요
@WebMvcTest(excludeAutoConfiguration = SecurityAutoConfiguration.class)
class OrderControllerTest {

    @Test
    void createOrder() {
    }

    @Test
    void getOrder() {
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