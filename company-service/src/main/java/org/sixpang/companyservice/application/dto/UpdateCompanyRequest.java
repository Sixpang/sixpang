package org.sixpang.companyservice.application.dto;

import org.sixpang.companyservice.domain.model.CompanyType;

import java.util.UUID;

public record UpdateCompanyRequest(
       String name,
       CompanyType type,
       String address,
       UUID hubId

) {
}
