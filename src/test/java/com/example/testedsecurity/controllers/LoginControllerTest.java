package com.example.testedsecurity.controllers;

import com.example.testedsecurity.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void deleteDataAfter() {
        userRepository.deleteAll();
    }

    @BeforeEach
    public void deleteDataBefore() {
        userRepository.deleteAll();
    }

    @Test
    public void testWithActualCredentials() throws Exception {
        mockMvc.perform(post("/login").with(httpBasic("user", "p")))
                .andExpectAll(status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.token", is(notNullValue())));
    }

    @Test
    public void testWithWrongCredentials() throws Exception {
        mockMvc.perform(post("/login").with(httpBasic("user", "paasdasd")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testWithoutCredentials() throws Exception {
        mockMvc.perform(post("/login"))
                .andExpect(status().isUnauthorized());
    }

}