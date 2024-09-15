package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.CredentialsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.ItemsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.NotFoundException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.repository.ShoppingCartRepository;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.TokenService;

public class ShoppingCartServiceImplTest {

    private final ShoppingCartRepository shoppingCartRepository = Mockito.mock(ShoppingCartRepository.class);
    private final CredentialsClient credentialsClient = Mockito.mock(CredentialsClient.class);
    private final ItemsClient itemsClient = Mockito.mock(ItemsClient.class);
    private final TokenService tokenService = Mockito.mock(TokenService.class);

    private final ShoppingCartServiceImpl service = new ShoppingCartServiceImpl(
            shoppingCartRepository, credentialsClient, itemsClient, tokenService);

    @Test
    public void testAddUnauthorized() {
        UUID userId = UUID.randomUUID();
        ShoppingCartItemAddRequest request = new ShoppingCartItemAddRequest(1L, 1);

        when(credentialsClient.findById(userId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.add("Bearer token", userId, request));
    }

    @Test
    public void testRemoveNotFound() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;

        when(credentialsClient.findById(userId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.remove("Bearer token", userId, itemId));
    }
}