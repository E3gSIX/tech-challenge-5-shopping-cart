package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

public interface ShoppingCartController {
    ResponseEntity add(
            String authorization,
            UUID userId,
            ShoppingCartItemAddRequest request,
            UriComponentsBuilder uriComponentsBuilder
    );

    ResponseEntity remove(
            String authorization,
            UUID userId,
            Long itemId,
            UriComponentsBuilder uriComponentsBuilder
    );
}
