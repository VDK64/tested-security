package com.example.testedsecurity.controllers;

import com.example.testedsecurity.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql("/test-data.sql")
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testWithActualCredentials() throws Exception {
        mockMvc.perform(post("/login").with(httpBasic("test_user", "p")))
                .andExpectAll(status().isOk(),
                        content().contentType(APPLICATION_JSON),
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