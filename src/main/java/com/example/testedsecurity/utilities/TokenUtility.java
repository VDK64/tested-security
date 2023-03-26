package com.example.testedsecurity.utilities;

import com.example.testedsecurity.dtos.LoginResponseDto;
import com.example.testedsecurity.properties.RoleProperties;
import com.example.testedsecurity.security.entities.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;

import static com.example.testedsecurity.properties.LoginProperties.CLAIM_SCOPE;
import static com.example.testedsecurity.properties.LoginProperties.ISSUER;
import static com.example.testedsecurity.properties.RoleProperties.ROLE_PREFIX;
import static com.example.testedsecurity.properties.StringProperties.WHITE_SPACE;

@Component
@RequiredArgsConstructor
public class TokenUtility {

    private final JwtEncoder jwtEncoder;

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(WHITE_SPACE));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authentication.getName())
                .claim(CLAIM_SCOPE, scope)
                .build();
        JwtEncoderParameters encoderParameters
                = JwtEncoderParameters.from(claims);
        return jwtEncoder.encode(encoderParameters).getTokenValue();
    }

    public String generateToken(Collection<Role> roles, String authenticationName) {
        Instant now = Instant.now();

        String scope = roles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE_PREFIX + role))
                .map(SimpleGrantedAuthority::getAuthority)
                .collect(Collectors.joining(WHITE_SPACE));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(authenticationName)
                .claim(CLAIM_SCOPE, scope)
                .build();

        JwtEncoderParameters encoderParameters
                = JwtEncoderParameters.from(claims);
        return jwtEncoder.encode(encoderParameters).getTokenValue();
    }

}
