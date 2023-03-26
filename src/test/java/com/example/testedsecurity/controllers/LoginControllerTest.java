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

    private static final String LOGIN_URL = "/login";

    private static final String TEST_USERNAME = "test_user";

    private static final String USER_USERNAME = "user";

    private static final String PROPER_PASSWORD = "p";

    private static final String WRONG_PASSWORD = "paasdasd";

    private static final String EXCTRACT_TOKE_FROM_JSON = "$.token";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testWithActualCredentials() throws Exception {
        mockMvc.perform(post(LOGIN_URL).with(httpBasic(TEST_USERNAME, PROPER_PASSWORD)))
                .andExpectAll(status().isOk(),
                        content().contentType(APPLICATION_JSON),
                        jsonPath(EXCTRACT_TOKE_FROM_JSON, is(notNullValue())));
    }

    @Test
    public void testWithWrongCredentials() throws Exception {
        mockMvc.perform(post(LOGIN_URL).with(httpBasic(USER_USERNAME, WRONG_PASSWORD)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testWithoutCredentials() throws Exception {
        mockMvc.perform(post(LOGIN_URL))
                .andExpect(status().isUnauthorized());
    }

}