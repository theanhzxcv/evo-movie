package com.evo.identity.infrastructure.adapter.keycloak;

public class KeycloakEndpoints {
    public static String issuer(String baseUrl, String realm) {
        return baseUrl + "/realms/" + realm;
    }

    public static String tokenEndpoint(String baseUrl, String realm) {
        return baseUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public static String userRegistration(String baseUrl, String realm) {
        return baseUrl + "/admin/realms/" + realm + "/users";
    }

    public static String resetPassword(String baseUrl, String realm, String userId) {
        return baseUrl + "/admin/realms/" + realm + "/users/" + userId + "/reset-password";
    }

    public static String userByEmail(String baseUrl, String realm, String email) {
        return baseUrl + "/admin/realms/" + realm + "/users?email=" + email;
    }

    public static String userById(String baseUrl, String realm, String userId) {
        return baseUrl + "/admin/realms/" + realm + "/users/" + userId;
    }
}
