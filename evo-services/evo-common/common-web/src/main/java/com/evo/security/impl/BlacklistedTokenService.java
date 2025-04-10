package com.evo.security.impl;

import com.evo.security.BlacklistedToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlacklistedTokenService implements BlacklistedToken {

    private final RedisTemplate<String, String> redisTemplate;
    private final static String INVALID_REFRESH_TOKEN_CACHE = "invalid-refresh-token";
    private final static String INVALID_TOKEN_CACHE = "invalid-access-token";

    @Override
    public void blacklistedAccessToken(String accessToken, long expirationDuration) {
        redisTemplate.opsForValue().set(accessToken,
                INVALID_TOKEN_CACHE,
                expirationDuration,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void blacklistedRefreshToken(String refreshToken, long expirationDuration) {
        redisTemplate.opsForValue().set(refreshToken,
                INVALID_REFRESH_TOKEN_CACHE,
                expirationDuration,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
