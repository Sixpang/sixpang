package org.sixpang.productservice.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 상품명입니다."),
    INVENTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "재고를 찾을 수 없습니다."),
    INVALID_INVENTORY_QUANTITY(HttpStatus.BAD_REQUEST, "재고 수량은 1 이상이어야 합니다."),
    INSUFFICIENT_INVENTORY(HttpStatus.BAD_REQUEST, "재고가 부족합니다.");

    private final HttpStatus status;
    private final String message;
}
