package org.sixpang.deliveryservice.exception;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.ErrorCode;

public class DeliveryException extends CustomException {
    public DeliveryException(ErrorCode errorCode) {
        super(errorCode);
    }
}
