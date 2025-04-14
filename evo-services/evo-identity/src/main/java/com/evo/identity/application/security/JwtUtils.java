package com.evo.identity.application.security;

import com.evo.identity.application.enums.ETokenExpiration;
import com.evo.identity.domain.User;
import com.evo.identity.infrastructure.adapter.keycloak.KeycloakEndpoints;
import com.evo.identity.infrastructure.adapter.keycloak.KeycloakProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtDecoder jwtDecoder;
    private final RSAKeysUtil rsaKeysUtil;
    private final KeycloakProperties keycloakProperties;

    public String generateToken(User user, long expirationTime) throws Exception {
        PrivateKey privateKey = rsaKeysUtil.getPrivateKey();
        return Jwts.builder()
                .setSubject(user.getUserName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .setId(UUID.randomUUID().toString())
                .claim("user_id", user.getId())
//                .claim("role", buildScope(userDetails))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public String generateAccessToken(User user) throws Exception {
        return generateToken(user, ETokenExpiration.ACCESS_TOKEN.value * 1000L);
    }

    public String generateRefreshToken(User user) throws Exception {
        return generateToken(user, ETokenExpiration.REFRESH_TOKEN.value * 1000L);
    }

    public Claims extractClaims(String token) {
        try {
            PublicKey publicKey = rsaKeysUtil.getPublicKey();

            return Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String extractUserName(String token) {
        return extractClaims(token).getSubject();
    }

    public Long getKeycloakJwtExpiration(String token) {
        JwtDecoder decoder = JwtDecoders.fromIssuerLocation(
                KeycloakEndpoints.issuer(keycloakProperties.getAuthServerUrl(), keycloakProperties.getRealm()));
        Jwt jwt = decoder.decode(token);
        Instant now = Instant.now();
        Instant expiration = jwt.getExpiresAt();

        return Duration.between(now, expiration).getSeconds();
    }

    public Long getExpirationTime(String token) {
        long expirationDate = extractClaims(token).getExpiration().getTime();
        long now = System.currentTimeMillis();

        return Math.max(0, (expirationDate - now) / 1000L);
    }
}
