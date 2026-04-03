package org.sixpang.companyservice.application.dto;

import org.sixpang.companyservice.domain.model.CompanyType;

import java.util.UUID;

public record CompanySearchRequest(
        String name,
        String address,
        CompanyType type,
        UUID hubId
) {
}
