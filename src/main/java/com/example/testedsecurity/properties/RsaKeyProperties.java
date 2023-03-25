package com.example.testedsecurity.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import static com.example.testedsecurity.properties.ConfigurationProperties.RSA_PREFIX;

@ConfigurationProperties(prefix = RSA_PREFIX)
public record RsaKeyProperties(RSAPublicKey publicKey, RSAPrivateKey privateKey) {
}
