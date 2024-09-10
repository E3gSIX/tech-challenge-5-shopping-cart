package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.impl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.PaymentIntegrityResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ShoppingCartResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.ShoppingCartService;

public class ShoppingCartControllerImplTest {

    private final ShoppingCartService shoppingCartService = mock(ShoppingCartService.class);
    private final ShoppingCartControllerImpl controller = new ShoppingCartControllerImpl(shoppingCartService);

    @Test
    public void testAdd() {
        String authorization = "Bearer token";
        UUID userId = UUID.randomUUID();
        ShoppingCartItemAddRequest request = new ShoppingCartItemAddRequest(1L, 2);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        // Ensure the stubbing is correctly completed
        when(shoppingCartService.add(authorization, userId, request)).thenReturn(1L);

        ResponseEntity<?> response = controller.add(authorization, userId, request, uriComponentsBuilder);
        assertEquals(201, response.getStatusCode().value());
    }

    @Test
    public void testRemove() {
        String authorization = "Bearer token";
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        when(shoppingCartService.remove(authorization, userId, itemId)).thenReturn(1L);

        ResponseEntity<?> response = controller.remove(authorization, userId, itemId, uriComponentsBuilder);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    public void testFindById() {
        String authorization = "Bearer token";
        Long id = 1L;
        UUID userId = UUID.randomUUID();
        ShoppingCartResponse responseMock = mock(ShoppingCartResponse.class);

        when(shoppingCartService.findById(authorization, userId, id)).thenReturn(responseMock);

        ResponseEntity<ShoppingCartResponse> response = controller.findById(authorization, id, userId);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(responseMock, response.getBody());
    }

    @Test
    public void testCheckIntegrityForPayment() {
        String authorization = "Bearer token";
        Long id = 1L;
        UUID userId = UUID.randomUUID();
        PaymentIntegrityResponse responseMock = mock(PaymentIntegrityResponse.class);

        when(shoppingCartService.checkPaymentIntegrity(authorization, userId, id)).thenReturn(responseMock);

        ResponseEntity<PaymentIntegrityResponse> response = controller.checkIntegrityForPayment(authorization, id, userId);
        assertEquals(200, response.getStatusCode().value());
        assertEquals(responseMock, response.getBody());
    }

    @Test
    public void testConclude() {
        String authorization = "Bearer token";
        Long id = 1L;
        UUID userId = UUID.randomUUID();

        doNothing().when(shoppingCartService).conclude(authorization, userId, id);

        ResponseEntity<?> response = controller.conclude(authorization, id, userId);
        assertEquals(200, response.getStatusCode().value());
    }
}