package com.example.testedsecurity.services;

import com.example.testedsecurity.dtos.LoginResponseDto;
import org.springframework.security.core.Authentication;

public interface LoginService {

    LoginResponseDto login(Authentication authentication);

}
