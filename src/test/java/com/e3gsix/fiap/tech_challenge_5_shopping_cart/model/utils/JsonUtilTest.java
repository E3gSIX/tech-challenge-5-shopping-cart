package com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtilTest {

    @Test
    public void testCreateObjectMapper() {
        ObjectMapper objectMapper = JsonUtil.createObjectMapper();
        assertNotNull(objectMapper);
    }
}