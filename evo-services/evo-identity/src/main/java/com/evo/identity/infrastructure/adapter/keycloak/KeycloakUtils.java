package com.evo.identity.infrastructure.adapter.keycloak;

import com.evo.constants.ErrConstants;
import com.evo.exception.AppException;
import com.evo.identity.domain.command.UserAuthenticationCmd;
import com.evo.identity.domain.command.UserDetailCmd;
import com.evo.identity.domain.command.UserRegistrationCmd;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeycloakUtils {

    private final RestTemplate restTemplate;
    private final KeycloakProperties keycloakProperties;

    public Map<String, Object> loginWithKeycloak(UserAuthenticationCmd cmd) {
        String loginUrl = KeycloakEndpoints.tokenEndpoint(keycloakProperties.getAuthServerUrl(), keycloakProperties.getRealm());

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());
        body.add("username", cmd.getUserName());
        body.add("password", cmd.getUserPass());

        return sendLoginRequest(loginUrl, body);
    }

//    public void createUserWithKeycloak(UserCreationCmd cmd) {
//        String registerUrl = keycloakProperties.getAuthServerUrl() + "/admin/realms/" + keycloakProperties.getRealm() + "/users";
//        String adminToken = getAdminToken();
//
//        Map<String, Object> userPayload = new HashMap<>();
//        userPayload.put("username", cmd.getUsername());
//        userPayload.put("email", cmd.getEmail());
//        userPayload.put("firstName", cmd.getFirstName());
//        userPayload.put("lastName", cmd.getLastName());
//        userPayload.put("enabled", true);
//        userPayload.put("credentials", List.of(Map.of("type", "password", "value", cmd.getPassword(), "temporary", false)));
//
//        sendRegistrationRequest(registerUrl, adminToken, userPayload, HttpMethod.POST);
//    }

    public void registrationWithKeycloak(UserRegistrationCmd cmd) {
        String registerUrl = KeycloakEndpoints.userRegistration(keycloakProperties.getAuthServerUrl(), keycloakProperties.getRealm());
        String adminToken = getAdminToken();

        UserDetailCmd userDetailCmd = cmd.getUserDetailCmd();
        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("username", cmd.getUserName());
        userPayload.put("email", userDetailCmd.getEmail());
        userPayload.put("firstName", userDetailCmd.getFirstName());
        userPayload.put("lastName", userDetailCmd.getLastName());
        userPayload.put("enabled", true);
        userPayload.put("credentials", List.of(Map.of("type", "password", "value", cmd.getUserPass(), "temporary", false)));

        sendRegistrationRequest(registerUrl, adminToken, userPayload, HttpMethod.POST);
    }

//    public void updateKeycloakUser(String email, UserUpdateCmd cmd) {
//        String userId = getUserIdByEmail(email);
//        String updateUrl = keycloakProperties.getAuthServerUrl() + "/admin/realms/" + keycloakProperties.getRealm() + "/users/" + userId;
//        String adminToken = getAdminToken();
//
//        Map<String, Object> userPayload = new HashMap<>();
//        userPayload.put("username", cmd.getUsername());
//        userPayload.put("email", email);
//        userPayload.put("firstName", cmd.getFirstName());
//        userPayload.put("lastName", cmd.getLastName());
//        userPayload.put("enabled", true);
//
//        sendRegistrationRequest(updateUrl, adminToken, userPayload, HttpMethod.PUT);
//    }

//    public void changeKeycloakPassword(PasswordChangeCmd cmd) {
//        String email = getCurrentUserEmail();
//        String adminToken = getAdminToken();
//        String userId = getUserIdByEmail(email);
//
//        String url = keycloakProperties.getAuthServerUrl() +
//                "/admin/realms/" +
//                keycloakProperties.getRealm() +
//                "/users/" +
//                userId +
//                "/reset-password";
//
//        Map<String, Object> passwordPayload = Map.of(
//                "type", "password",
//                "value", cmd.getPassword(),
//                "temporary", false
//        );
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(adminToken);
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(passwordPayload, headers);
//
//        try {
//            restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
//        } catch (HttpClientErrorException e) {
//            throw new RuntimeException("Failed to change password: " + e.getMessage(), e);
//        }
//    }

//    public Map<String, String> refreshToken(String refreshToken) {
//        String tokenUrl = keycloakProperties.getAuthServerUrl()
//                + "/realms/"
//                + keycloakProperties.getRealm()
//                + "/protocol/openid-connect/token";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "refresh_token");
//        body.add("refresh_token", refreshToken);
//        body.add("client_id", keycloakProperties.getClientId());
//        body.add("client_secret", keycloakProperties.getClientSecret());
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);
//        Map<String, String> responseBody = null;
//        if (response.getStatusCode() == HttpStatus.OK) {
//            responseBody = response.getBody();
//        }
//
//        return responseBody;
//    }

    private Map<String, Object> sendLoginRequest(String url, MultiValueMap<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new AppException(ErrConstants.AUTH_ERROR_003);
        } catch (Exception e) {
            throw new AppException(ErrConstants.SYSTEM_ERROR_001);
        }
    }

    private void sendRegistrationRequest(String url, String adminToken, Map<String, Object> payload, HttpMethod method) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, method, new HttpEntity<>(payload, headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new AppException(ErrConstants.AUTH_ERROR_004);
            }
        } catch (HttpClientErrorException e) {
            throw new AppException(ErrConstants.SYSTEM_ERROR_001);
        }
    }

//    private String getUserIdByEmail(String email) {
//        String searchUrl = keycloakProperties.getAuthServerUrl() + "/admin/realms/" + keycloakProperties.getRealm() + "/users?email=" + email;
//        String adminToken = getAdminToken();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(adminToken);
//
//        ResponseEntity<List> response = restTemplate.exchange(searchUrl, HttpMethod.GET, new HttpEntity<>(headers), List.class);
//        if (response.getBody() == null || response.getBody().isEmpty()) {
//            throw new AppException(ErrorCode.USER_NOT_FOUND);
//        }
//        return ((Map<String, Object>) response.getBody().get(0)).get("id").toString();
//    }

    private String getAdminToken() {
        String tokenUrl = keycloakProperties.getAuthServerUrl() + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", keycloakProperties.getClientId());
        body.add("client_secret", keycloakProperties.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, new HttpEntity<>(body, headers), Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get admin token");
        }
        return response.getBody().get("access_token").toString();
    }

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
