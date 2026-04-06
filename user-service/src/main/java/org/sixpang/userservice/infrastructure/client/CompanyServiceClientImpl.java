package org.sixpang.userservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyServiceClientImpl implements CompanyServiceClient {

    private final CompanyServiceClient companyClient;

    @Override
    public boolean exists(UUID companyId) {
        return companyClient.exists(companyId);
    }
}