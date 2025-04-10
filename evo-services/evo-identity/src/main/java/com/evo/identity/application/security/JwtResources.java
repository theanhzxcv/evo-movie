package com.evo.identity.application.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwtResources {
    private final RSAKeysUtil rsaKeysUtil;

    @GetMapping("/certificate/.well-known/jwks.json")
    public Map<String, Object> publicKey() throws Exception {
        return this.rsaKeysUtil.jwkSet().toJSONObject();
    }
}
