package org.sixpang.userservice.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PageResponseDto<T> {

    private T content;
    private Meta meta;

    @Getter
    @AllArgsConstructor
    public static class Meta {
        private long totalElements;
        private int totalPages;
        private int page;
    }
}