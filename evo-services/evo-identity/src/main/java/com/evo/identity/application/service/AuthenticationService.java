package com.evo.identity.application.service;

import com.evo.identity.application.model.AuthenticationReqModel;
import com.evo.identity.application.model.AuthenticationResModel;
import com.evo.identity.application.model.RegistrationReqModel;

import java.util.Map;
import java.util.UUID;

public interface AuthenticationService {

    AuthenticationResModel signIn(AuthenticationReqModel model);

    Map<String, UUID> signUp(RegistrationReqModel model);

    Map<String, Long> enableTfa();

    Map<String, Long> disableTfa();

    AuthenticationResModel verifyTfa(int tfaCode);

    Map<String, String> sendVerificationEmail();

    Map<String, Long> verifyEmail(String verifyKey);

    Map<String, Long> signOut();

    AuthenticationResModel refreshToken(String refreshToken);
}
