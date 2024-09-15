package com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.config;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.StandardError;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.StandardErrorException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.utils.JsonUtil;

import feign.Response;

class FeignDecoderTest {

    @Test
    public void testDecode() throws Exception {
        FeignDecoder feignDecoder = new FeignDecoder();
        Response response = Mockito.mock(Response.class);
        Response.Body body = Mockito.mock(Response.Body.class);

        String json = JsonUtil.createObjectMapper().writeValueAsString(new StandardError(null, 400, "Bad Request", "Error message", "/path"));
        when(body.asInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        when(response.body()).thenReturn(body);

        assertThrows(StandardErrorException.class, () -> feignDecoder.decode("methodKey", response));
    }

}
