package com.example.testedsecurity.controllers;

import com.example.testedsecurity.dtos.RegisterRequestDto;
import com.example.testedsecurity.dtos.RegisterResponseDto;
import com.example.testedsecurity.services.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.testedsecurity.properties.RegisterProperties.REGISTER_REQUEST_MAPPING;

@RestController
@RequestMapping(REGISTER_REQUEST_MAPPING)
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @PostMapping
    public ResponseEntity<RegisterResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(registerService.register(registerRequestDto));
    }

}
