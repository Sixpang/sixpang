package org.sixpang.authservice.application.security;

import java.util.UUID;

public interface TokenService {
    String createToken(UUID userId, String role);
}