package org.sixpang.userservice.exception;

import org.sixpang.commonserver.global.CustomException;
import org.sixpang.commonserver.global.ErrorCode;

public class UserException extends CustomException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
