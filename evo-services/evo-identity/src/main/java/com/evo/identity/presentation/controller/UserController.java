package com.evo.identity.presentation.controller;

import com.evo.util.EvoSecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/greet")
    public String greet() {
        return "Hello " + EvoSecurityUtils.getCurrentUser();
    }
}
