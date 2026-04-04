package org.sixpang.userservice.domain.model.enums;

import org.sixpang.userservice.exception.UserErrorCode;
import org.sixpang.userservice.exception.UserException;

public enum UserStatus {
    PENDING,  //대기
    APPROVED, //승인
    REJECTED; //거부

    //코드리뷰 :서비스에서 문자열 비교(하드코딩)말고 상태 판단을 위임하기 위한 메서드 (isApproved 로쓰기위해서)
    public boolean isApproved() {
        return this == APPROVED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }


    // 승인 가능 여부 검증 (상태 기반 책임 위임)
    public void validateApprove() {
        if (this == APPROVED) {
            throw new UserException(UserErrorCode.ALREADY_APPROVED);
        }
        if (this != PENDING) {
            throw new UserException(UserErrorCode.CANNOT_APPROVE);
        }
    }

    // 거절 가능 여부 검증 (상태 기반 책임 위임)
    public void validateReject() {
        if (this == REJECTED) {
            throw new UserException(UserErrorCode.ALREADY_REJECTED);
        }
        if (this != PENDING) {
            throw new UserException(UserErrorCode.CANNOT_REJECT);
        }
    }
}