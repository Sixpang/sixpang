package org.sixpang.companyservice.application.dto;

import org.sixpang.companyservice.domain.model.CompanyType;

import java.util.UUID;

public record CreateCompanyRequest (
        String name,
        String address,
        CompanyType type,
        UUID hubId
){
}
