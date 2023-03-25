package com.example.testedsecurity.services;

import com.example.testedsecurity.dtos.RegisterRequestDto;
import com.example.testedsecurity.dtos.RegisterResponseDto;

public interface RegisterService {

    RegisterResponseDto register(RegisterRequestDto registerRequestDto);

}
