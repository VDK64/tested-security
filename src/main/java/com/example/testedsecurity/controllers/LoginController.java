package com.example.testedsecurity.controllers;

import com.example.testedsecurity.dtos.LoginResponseDto;
import com.example.testedsecurity.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.testedsecurity.properties.LoginProperties.LOGIN_REQUEST_MAPPING;

@RequiredArgsConstructor
@RestController
@RequestMapping(LOGIN_REQUEST_MAPPING)
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResponseDto> login(Authentication authentication) {
        return ResponseEntity.ok(loginService.login(authentication));
    }

}
