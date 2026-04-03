package org.sixpang.userservice.domain.model.enums;

public enum UserStatus {
    PENDING,  //대기
    APPROVED, //승인
    REJECTED; //거부

    //코드리뷰 :서비스에서 문자열비요(하드코딩)말고 상태 판단을 위임하기 위한 메서드 (isApproved 로쓰기위해서)
    public boolean isApproved() {
        return this == APPROVED;
    }

    public boolean isRejected() {
        return this == REJECTED;
    }
}
