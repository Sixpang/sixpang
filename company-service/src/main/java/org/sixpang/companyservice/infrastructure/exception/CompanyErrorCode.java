package org.sixpang.companyservice.infrastructure.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sixpang.commonserver.global.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CompanyErrorCode implements ErrorCode {

    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND,"업체를 찾을 수 없습니다."),
    COMPANY_ALREADY_EXISTS(HttpStatus.CONFLICT,"이미 존재하는 업체입니다."),
    UNAUTHORIZED_COMPANY_ACESS(HttpStatus.FORBIDDEN,"해당 업체에 대한 권한이 없습니다.");

    private final HttpStatus status;
    private final String message;

}
