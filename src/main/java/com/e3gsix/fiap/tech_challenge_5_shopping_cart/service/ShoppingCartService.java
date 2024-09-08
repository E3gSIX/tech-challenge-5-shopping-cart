package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;

import java.util.UUID;

public interface ShoppingCartService {
    Long add(String authorization, UUID userId, ShoppingCartItemAddRequest request);
}
