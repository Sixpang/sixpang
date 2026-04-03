package org.sixpang.orderservice.exception;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.ErrorCode;

public class OrderException extends CustomException {
    public OrderException(ErrorCode errorCode) {
        super(errorCode);
    }
}
