package com.evo.identity.infrastructure.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ApplicationProperties {
    @Value("${encryption.key}")
    private String encryptionKey;
}
