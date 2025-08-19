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

    @PutMapping("/tfa/enable")
    public Response<Object> enableTfa() {
        AuthenticationService authenticationService = authServiceFactory.getAuthService();
        return Response.of(authenticationService.enableTfa());
    }

    @PutMapping("/tfa/disable")
    public Response<Object> disableTfa() {
        AuthenticationService authenticationService = authServiceFactory.getAuthService();
        return Response.of(authenticationService.disableTfa());
    }

    @PostMapping("/tfa/verify")
    public Response<AuthenticationResModel> verifyTfa(@RequestParam int tfaCode) {
        AuthenticationService authenticationService = authServiceFactory.getAuthService();
        return Response.of(authenticationService.verifyTfa(tfaCode));
    }

    @PostMapping("/email/verification/send")
    public Response<Object> sendVerificationEmail() {
        AuthenticationService authenticationService = authServiceFactory.getAuthService();
        return Response.of(authenticationService.sendVerificationEmail());
    }

    @PostMapping("/email/verification/verify")
    public Response<Object> verifyEmail(@RequestParam String verifyKey) {
        AuthenticationService authenticationService = authServiceFactory.getAuthService();
        return Response.of(authenticationService.verifyEmail(verifyKey));
    }

    @PostMapping("/sign-out")
    public Response<Object> signOut() {
        AuthenticationService authenticationService = authServiceFactory.getAuthService();
        return Response.of(authenticationService.signOut());
    }
}
