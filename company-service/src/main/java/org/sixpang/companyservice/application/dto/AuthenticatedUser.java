package org.sixpang.companyservice.application.dto;

import org.sixpang.companyservice.domain.model.UserRole;

import java.util.UUID;


//나중에 JWT이 구현되면 권한 검사를 하기 위한 값들
public record AuthenticatedUser(
        UUID userId,
        UserRole role,
        UUID hubId
) {
}
