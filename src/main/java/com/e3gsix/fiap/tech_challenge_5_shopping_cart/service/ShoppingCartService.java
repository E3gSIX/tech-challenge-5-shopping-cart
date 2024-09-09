package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.PaymentIntegrityResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ShoppingCartResponse;

import java.util.UUID;

public interface ShoppingCartService {
    Long add(String authorization, UUID userId, ShoppingCartItemAddRequest request);

    Long remove(String authorization, UUID userId, Long itemId);

    ShoppingCartResponse findById(String authorization, UUID userId, Long id);

    void conclude(String authorization, UUID userId, Long id);

    PaymentIntegrityResponse checkPaymentIntegrity(String authorization, UUID userId, Long id);
}
