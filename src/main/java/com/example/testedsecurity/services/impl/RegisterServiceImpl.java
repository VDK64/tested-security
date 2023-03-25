package com.example.testedsecurity.services.impl;

import com.example.testedsecurity.dtos.RegisterRequestDto;
import com.example.testedsecurity.dtos.RegisterResponseDto;
import com.example.testedsecurity.exceptions.UserRegistrationException;
import com.example.testedsecurity.repositories.UserRepository;
import com.example.testedsecurity.security.entities.Role;
import com.example.testedsecurity.security.entities.User;
import com.example.testedsecurity.services.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.example.testedsecurity.properties.RegisterProperties.USER_REGISTRATION_EXCEPTION_MESSAGE;
import static com.example.testedsecurity.security.entities.Role.USER;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        String password = registerRequestDto.password();
        String username = registerRequestDto.username();
        Collection<Role> roles = List.of(USER);
        User user = new User(null, roles, passwordEncoder.encode(password), username, true,
                true, true, true);
        try {
            userRepository.save(user);

        } catch (DataIntegrityViolationException exception) {
            throw new UserRegistrationException(String.format(USER_REGISTRATION_EXCEPTION_MESSAGE, username));
        }
        return new RegisterResponseDto(username);
    }

}
