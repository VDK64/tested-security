package com.example.testedsecurity.controllers;

import com.example.testedsecurity.dtos.RegisterRequestDto;
import com.example.testedsecurity.dtos.RegisterResponseDto;
import com.example.testedsecurity.repositories.UserRepository;
import com.example.testedsecurity.security.entities.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.example.testedsecurity.Utility.asJsonString;
import static com.example.testedsecurity.properties.RegisterProperties.REGISTER_REQUEST_MAPPING;
import static com.example.testedsecurity.security.entities.Role.ADMIN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/test-data.sql")
class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void successRegistrationTest() throws Exception {
        String requestBody = asJsonString(new RegisterRequestDto("test", "test"));
        String responseBody = asJsonString(new RegisterResponseDto("test"));
        mockMvc.perform(post(REGISTER_REQUEST_MAPPING)
                        .accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpectAll(status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        content().json(responseBody));
    }

    @Test
    public void wrongRegistrationTest() throws Exception {
        mockMvc.perform(post(REGISTER_REQUEST_MAPPING)
                        .accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                )
                .andExpectAll(status().isBadRequest());
    }

    @Test
    public void duplicateUsernameRegistrationTest() throws Exception {
        User savedUser = userRepository.save(new User(null, List.of(ADMIN), "p", "duplicate",
                true, true, true, true));

        String requestBody = asJsonString(new RegisterRequestDto("duplicate", "p"));
        mockMvc.perform(post(REGISTER_REQUEST_MAPPING)
                        .accept(APPLICATION_JSON).contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

}