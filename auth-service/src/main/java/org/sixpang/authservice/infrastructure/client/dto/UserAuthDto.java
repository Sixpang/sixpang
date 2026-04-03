package org.sixpang.authservice.infrastructure.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthDto {

    private UUID id;
    private String email;
    private String password;
    private String name;
    private String role;
}
