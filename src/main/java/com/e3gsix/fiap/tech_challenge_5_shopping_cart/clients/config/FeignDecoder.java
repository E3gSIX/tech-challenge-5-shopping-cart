package com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.config;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.StandardError;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.StandardErrorException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.utils.JsonUtil;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FeignDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String body = convertInputStreamToString(response.body().asInputStream());
            StandardError standardError = JsonUtil.createObjectMapper().readValue(body, StandardError.class);

            throw new StandardErrorException(standardError);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertInputStreamToString(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o corpo da resposta", e);
        }
    }
}