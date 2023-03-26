package com.example.testedsecurity.services.impl;

import com.example.testedsecurity.dtos.LoginResponseDto;
import com.example.testedsecurity.services.LoginService;
import com.example.testedsecurity.utilities.TokenUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final TokenUtility tokenUtility;

    @Override
    public LoginResponseDto login(Authentication authentication) {
        return new LoginResponseDto(tokenUtility.generateToken(authentication));
    }

}
