package com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request;

public record ShoppingCartItemAddRequest(
        Long itemId,
        Integer quantity
) {
}
