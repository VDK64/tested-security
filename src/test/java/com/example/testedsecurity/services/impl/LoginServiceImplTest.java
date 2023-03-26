package com.example.testedsecurity.services.impl;

import com.example.testedsecurity.dtos.LoginResponseDto;
import com.example.testedsecurity.services.LoginService;
import com.example.testedsecurity.utilities.TokenUtility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class LoginServiceImplTest {

    @Autowired
    private LoginService underTest;

    @MockBean
    private TokenUtility tokenUtility;

    @Test
    public void callLoginGenerateTokenTest() {
        Authentication mockedAuthentication = mock(Authentication.class);
        String token = "token";
        when(tokenUtility.generateToken(mockedAuthentication)).thenReturn(token);

        LoginResponseDto result = underTest.login(mockedAuthentication);
        assertEquals(token, result.token());
    }

    @Test
    public void callLoginTest() {
        Authentication mockedAuthentication = mock(Authentication.class);
        when(tokenUtility.generateToken(mockedAuthentication)).thenReturn("");

        LoginResponseDto result = underTest.login(mockedAuthentication);
        verify(tokenUtility, times(1)).generateToken(mockedAuthentication);
    }

}