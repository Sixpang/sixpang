package org.sixpang.hubservice.exception;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.ErrorCode;

public class HubException extends CustomException {
    public HubException(ErrorCode errorCode) {
        super(errorCode);
    }
}
