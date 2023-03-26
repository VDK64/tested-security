package com.example.testedsecurity.security.configs;

import com.example.testedsecurity.properties.RsaKeyProperties;
import com.example.testedsecurity.repositories.UserRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

import static com.example.testedsecurity.properties.BookProperties.BOOK_REQUEST_MAPPING;
import static com.example.testedsecurity.properties.BookProperties.BOOK_REQUEST_MAPPING_WITH_PATH_VARIABLE;
import static com.example.testedsecurity.properties.ExceptionsProperties.USER_NOT_FOUND_MESSAGE;
import static com.example.testedsecurity.properties.RegisterProperties.REGISTER_REQUEST_MAPPING;
import static com.example.testedsecurity.properties.StringProperties.EMPTY;
import static com.example.testedsecurity.security.entities.Role.ADMIN;
import static com.example.testedsecurity.security.entities.Role.USER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableConfigurationProperties(RsaKeyProperties.class)
public class SecurityConfig {

    private final UserRepository userRepository;

    private final RsaKeyProperties rsaKeyProperties;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(REGISTER_REQUEST_MAPPING).permitAll()
                                .requestMatchers(GET, BOOK_REQUEST_MAPPING).hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(GET, BOOK_REQUEST_MAPPING_WITH_PATH_VARIABLE).hasAnyRole(ADMIN.name(), USER.name())
                                .requestMatchers(POST, BOOK_REQUEST_MAPPING).hasRole(ADMIN.name())
                                .requestMatchers(DELETE, BOOK_REQUEST_MAPPING_WITH_PATH_VARIABLE).hasRole(ADMIN.name())
                                .requestMatchers(PUT, BOOK_REQUEST_MAPPING).hasRole(ADMIN.name())
                                .anyRequest().authenticated()
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
                .httpBasic();//todo Disable httpBasic authentication and provide username/password authentication via LoginController

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix(EMPTY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public UserDetailsService userDetails() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_MESSAGE, username)));
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeyProperties.publicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        RSAKey rsaKey = new RSAKey.Builder(rsaKeyProperties.publicKey())
                .privateKey(rsaKeyProperties.privateKey())
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

}
