package com.evo.identity.application.security;

import com.evo.identity.application.enums.ETokenExpiration;
import com.evo.identity.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtDecoder jwtDecoder;
    private final RSAKeysUtil rsaKeysUtil;

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

    public Long getExpirationTime(String token) {
        long expirationDate = extractClaims(token).getExpiration().getTime();
        long now = System.currentTimeMillis();

        return Math.max(0, (expirationDate - now) / 1000L);
    }
}
