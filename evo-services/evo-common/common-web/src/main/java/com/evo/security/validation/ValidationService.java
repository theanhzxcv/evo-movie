package com.evo.security.validation;

public interface ValidationService {

    void validateCurrentUser();

    void validateAccessToken(String username);

    void validateTfaToken(String username);

    void validateResetToken();
}
