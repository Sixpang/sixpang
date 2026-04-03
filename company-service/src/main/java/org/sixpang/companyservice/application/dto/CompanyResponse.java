package org.sixpang.companyservice.application.dto;

import org.sixpang.companyservice.domain.model.Company;
import org.sixpang.companyservice.domain.model.CompanyType;

import java.util.UUID;

public record CompanyResponse(
        UUID companyId,
        String name,
        CompanyType type,
        String address,
        UUID hubId

) {
    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getType(),
                company.getAddress(),
                company.getHubId()
        );
    }
}
