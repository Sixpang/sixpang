package org.sixpang.hubservice.exception;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.ErrorCode;

public class RouteException extends CustomException {
    public RouteException(ErrorCode errorCode) {
        super(errorCode);
    }
}
