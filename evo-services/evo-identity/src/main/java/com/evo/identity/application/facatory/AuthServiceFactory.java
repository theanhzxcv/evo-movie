package com.evo.identity.application.facatory;

import com.evo.identity.application.service.AuthenticationService;
import com.evo.identity.application.service.impl.ApplicationAuthenticationServiceImpl;
import com.evo.identity.application.service.impl.KeycloakAuthenticationServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceFactory {
    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    private final KeycloakAuthenticationServiceImpl keycloakAuthService;
    private final ApplicationAuthenticationServiceImpl applicationAuthService;

    public AuthServiceFactory(KeycloakAuthenticationServiceImpl keycloakAuthService,
                              ApplicationAuthenticationServiceImpl applicationAuthService) {
        this.keycloakAuthService = keycloakAuthService;
        this.applicationAuthService = applicationAuthService;
    }

    public AuthenticationService getAuthService() {
        if (isKeycloakEnabled) {
            return keycloakAuthService;
        } else {
            return applicationAuthService;
        }
    }
}
