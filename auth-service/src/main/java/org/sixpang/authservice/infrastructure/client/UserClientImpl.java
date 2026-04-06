package org.sixpang.authservice.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.sixpang.authservice.application.client.UserClient;
import org.sixpang.authservice.application.client.dto.UserAuthDto;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {

    private final FeignUserClient feignUserClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UserAuthDto getUserByEmail(String email) {

        Map<String, Object> response = feignUserClient.getUserByEmail(email);

        Map<String, Object> data = (Map<String, Object>) response.get("data");

        return objectMapper.convertValue(data, UserAuthDto.class);
    }
}