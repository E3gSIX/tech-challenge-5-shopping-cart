package com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response;

import java.math.BigDecimal;

public record ShoppingCartItemResponse(
        String name,
        BigDecimal price,
        Integer quantity
) {
}
