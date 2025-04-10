package com.evo.security;

import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JwkAuthManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {
    private final Map<String, String> issuers;
    private final Map<String, AuthenticationManager> authenticationManager = new HashMap<>();
    private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();

    @Override
    public AuthenticationManager resolve(HttpServletRequest request) {
        return this.authenticationManager.computeIfAbsent(toIssuerId(request), this::fromIssuer);
    }

    public JwkAuthManagerResolver(JwtProperties jwtProperties) {
        this.issuers = jwtProperties.getJwkSetUris();
    }

    private String toIssuerId(HttpServletRequest request) {
        String token = this.resolver.resolve(request);
        try {
            if (StringUtils.hasText((String) JWTParser.parse(token).getJWTClaimsSet().getClaim("user_id"))) {
                return "internal";
            } else if (StringUtils.hasText((String) JWTParser.parse(token).getJWTClaimsSet().getClaim("preferred_username"))) {
                return "sso";
            } else {
                throw new RuntimeException("INVALID_INPUT");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private AuthenticationManager fromIssuer(String issuerId) {
        return Optional.ofNullable(this.issuers.get(issuerId))
                .map(issuer -> NimbusJwtDecoder.withJwkSetUri(issuer).build())
                .map(JwtAuthenticationProvider::new)
                .orElseThrow(() -> new IllegalArgumentException("unknown issuer"))::authenticate;
    }
}
