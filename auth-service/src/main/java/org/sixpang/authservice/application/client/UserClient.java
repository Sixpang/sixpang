package org.sixpang.authservice.application.client;

import org.sixpang.authservice.application.client.dto.UserAuthDto;

public interface UserClient {
    UserAuthDto getUserByEmail(String email);
}