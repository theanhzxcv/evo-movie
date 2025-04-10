package com.evo.identity.domain.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfoCmd {
    private UUID id;
    private UUID userId;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireAt;
    private Long refreshTokenExpireAt;
    String type;
}
