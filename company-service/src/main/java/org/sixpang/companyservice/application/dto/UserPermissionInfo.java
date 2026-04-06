package org.sixpang.companyservice.application.dto;

import java.util.UUID;

public record UserPermissionInfo(
        UUID userId,
        String role,
        UUID hubId,
        UUID companyId
) {
}
