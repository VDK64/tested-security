package com.example.testedsecurity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Utility {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
