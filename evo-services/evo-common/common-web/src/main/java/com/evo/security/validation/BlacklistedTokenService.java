package com.evo.security.validation;

public interface BlacklistedTokenService {

    void blacklistedAccessToken(String accessToken, long expirationDuration);

    void blacklistedRefreshToken(String refreshToken, long expirationDuration);

    boolean isTokenBlacklisted(String token);
}
