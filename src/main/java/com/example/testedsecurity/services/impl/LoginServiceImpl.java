package com.example.testedsecurity.services.impl;

import com.example.testedsecurity.dtos.LoginResponseDto;
import com.example.testedsecurity.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import static com.example.testedsecurity.properties.LoginProperties.CLAIM_SCOPE;
import static com.example.testedsecurity.properties.LoginProperties.ISSUER;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final JwtEncoder jwtEncoder;

    @Override
    public LoginResponseDto login(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim(CLAIM_SCOPE, scope)
                .build();
        JwtEncoderParameters encoderParameters
                = JwtEncoderParameters.from(claims);
        return new LoginResponseDto(jwtEncoder.encode(encoderParameters).getTokenValue());
    }

}
