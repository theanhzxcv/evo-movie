package com.evo.identity.application.service;

import com.evo.identity.application.model.AuthenticationReqModel;
import com.evo.identity.application.model.AuthenticationResModel;
import com.evo.identity.application.model.RegistrationReqModel;

import java.util.Map;

public interface AuthenticationService {

    AuthenticationResModel signIn(AuthenticationReqModel model);

    Map<String, Long> signUp(RegistrationReqModel model);

    AuthenticationResModel tfaRequired();
}
