package com.evo.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.resource-server.jwt")
public class JwtProperties {
    private Map<String, String> jwkSetUris;
}
