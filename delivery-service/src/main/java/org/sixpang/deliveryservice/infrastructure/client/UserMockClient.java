package org.sixpang.deliveryservice.infrastructure.client;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Component
public class UserMockClient {

    void checkExists(UUID userId) {
        if(userId.getLeastSignificantBits() % 2  > 0) {
            throw new RuntimeException("user not exists");
        }
    }

}
