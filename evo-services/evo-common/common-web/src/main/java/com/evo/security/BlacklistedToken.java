package com.evo.security;

public interface BlacklistedToken {

    void blacklistedAccessToken(String accessToken, long expirationDuration);

    void blacklistedRefreshToken(String refreshToken, long expirationDuration);

    boolean isTokenBlacklisted(String token);
}
