package com.evo.identity.application.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class AuthenticationResModel {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpireAt;
    private Long refreshTokenExpireAt;
}
