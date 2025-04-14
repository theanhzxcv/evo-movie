package com.evo.configuration;

import com.evo.UserAuthentication;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.regex.Pattern;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetResource, Object requiredScope) {
        if (!(authentication instanceof UserAuthentication userAuthentication)) {
            throw new RuntimeException("NOT_SUPPORTED_AUTHENTICATION");
        }

        if (userAuthentication.isRoot().equals(1L)) {
            return true;
        }

        String requiredPermission = targetResource + ":" + requiredScope;

        return userAuthentication.getGrantedPermissions().stream()
                .anyMatch(permission -> Pattern.matches(permission, requiredPermission));
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
