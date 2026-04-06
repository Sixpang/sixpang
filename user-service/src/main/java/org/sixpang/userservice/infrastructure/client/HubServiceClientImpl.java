package org.sixpang.userservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.sixpang.userservice.application.client.HubClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubServiceClientImpl implements HubServiceClient {

    private final HubClient hubClient;

    @Override
    public boolean exists(UUID hubId) {
        return hubClient.exists(hubId);
    }
}