package com.evo.identity.presentation.controller;

import com.evo.identity.application.model.ChangePasswordReqModel;
import com.evo.identity.application.model.ProfileReqModel;
import com.evo.identity.application.model.ProfileResModel;
import com.evo.identity.application.service.UserService;
import com.evo.response.Response;
import com.evo.util.EvoSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/greet")
    public String greet() {
        return "Hello " + EvoSecurityUtils.getCurrentUserName();
    }

    @GetMapping("/profile")
    public Response<ProfileResModel> profile() {
        return Response.of(userService.profile());
    }

    @PostMapping("/profile/update")
    public Response<Object> profile(@RequestBody ProfileReqModel model) {
        return Response.of(userService.update(model));
    }

    @PutMapping("/profile/password")
    public Response<Object> profilePassword(@RequestBody ChangePasswordReqModel model) {
        return Response.of(userService.changePassword(model));
    }
}
