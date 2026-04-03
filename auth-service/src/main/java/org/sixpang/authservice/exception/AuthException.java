package org.sixpang.authservice.exception;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.ErrorCode;

public class AuthException extends CustomException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}