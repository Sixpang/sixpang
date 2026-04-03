package org.sixpang.deliveryservice.infrastructure.client;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HubMockClient {

    void checkExists(UUID hubId){
        if(hubId.getLeastSignificantBits() % 2 > 0){
            throw new RuntimeException("hub not exists");
        }
    }
}
