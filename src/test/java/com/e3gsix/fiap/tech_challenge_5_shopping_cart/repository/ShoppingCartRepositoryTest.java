package com.e3gsix.fiap.tech_challenge_5_shopping_cart.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCart;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.ShoppingCartStatus;

@SpringBootTest
public class ShoppingCartRepositoryTest {

    @Autowired
    private ShoppingCartRepository repository;

    @Test
    public void testFindTop1ByStatusAndUserId() {
        UUID userId = UUID.randomUUID();
        ShoppingCart cart = new ShoppingCart(userId);
        cart.setStatus(ShoppingCartStatus.ACTIVE);
        repository.save(cart);

        Optional<ShoppingCart> result = repository.findTop1ByStatusAndUserId(ShoppingCartStatus.ACTIVE, userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
    }
}