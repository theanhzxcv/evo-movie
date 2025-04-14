package com.evo.identity.presentation.controller;

import com.evo.identity.application.facatory.AuthServiceFactory;
import com.evo.identity.application.model.AuthenticationReqModel;
import com.evo.identity.application.model.AuthenticationResModel;
import com.evo.identity.application.model.RegistrationReqModel;
import com.evo.identity.application.service.AuthenticationService;
import com.evo.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthServiceFactory authServiceFactory;

    @PostMapping("/sign-in")
    public Response<AuthenticationResModel> signIn(@Valid @RequestBody AuthenticationReqModel model) {
        AuthenticationService authenticationService = authServiceFactory.getAuthService();
        return Response.of(authenticationService.signIn(model));
    }

    @PostMapping("/sign-up")
    public Response<Object> signUp(@Valid @RequestBody RegistrationReqModel model) {
        AuthenticationService authenticationService = authServiceFactory.getAuthService();
        return Response.of(authenticationService.signUp(model));
    }
}
