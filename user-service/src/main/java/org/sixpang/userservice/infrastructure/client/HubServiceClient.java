package org.sixpang.userservice.infrastructure.client;

import java.util.UUID;

public interface HubServiceClient {
    boolean exists(UUID hubId);
}
