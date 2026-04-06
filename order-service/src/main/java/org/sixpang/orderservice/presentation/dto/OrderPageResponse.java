package org.sixpang.orderservice.presentation.dto;

import lombok.Getter;
import org.sixpang.orderservice.domain.model.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class OrderPageResponse {

    private final List<OrderResponse> content;    // 실제 데이터 목록
    private final int page;           // 현재 페이지 번호
    private final int size;           // 페이즈 사이즈
    private final long totalElements;  // 전체 데이터 수
    private final int totalPages;     // 전체 페이지 수

    public OrderPageResponse(Page<Order> page) {
        this.content = page.getContent()
                .stream()
                .map(OrderResponse::fromEntity)
                .toList();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
    }
}
