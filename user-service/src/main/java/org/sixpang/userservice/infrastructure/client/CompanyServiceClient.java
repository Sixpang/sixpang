package org.sixpang.userservice.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyServiceClient {

    @GetMapping("/api/companies/{companyId}/exists")
    boolean exists(@PathVariable UUID companyId);
}