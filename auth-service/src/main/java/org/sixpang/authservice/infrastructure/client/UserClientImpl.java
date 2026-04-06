package org.sixpang.authservice.infrastructure.client;

import lombok.RequiredArgsConstructor;
import org.sixpang.authservice.application.client.UserClient;
import org.sixpang.authservice.application.client.dto.UserAuthDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

    private final FeignUserClient feignUserClient;

    @Override
    public UserAuthDto getUserByEmail(String email) {
        return feignUserClient.getUserByEmail(email).getData();
    }
}