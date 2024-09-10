package com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.ShoppingCartStatus;

public class ShoppingCartTest {

    @Test
    public void testAddItem() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
        ShoppingCartItem item = new ShoppingCartItem(1L, 2);
        cart.addItem(item);
        assertEquals(1, cart.getShoppingCartItems().size());
    }

    @Test
    public void testRemoveItem() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
        ShoppingCartItem item = new ShoppingCartItem(1L, 2);
        cart.addItem(item);
        cart.removeItem(1L);
        assertEquals(0, cart.getShoppingCartItems().size());
    }

    @Test
    public void testGetTotalQuantity() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
        cart.addItem(new ShoppingCartItem(1L, 2));
        cart.addItem(new ShoppingCartItem(2L, 3));
        assertEquals(5, cart.getTotalQuantity());
    }

    @Test
    public void testSetStatus() {
        ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
        cart.setStatus(ShoppingCartStatus.CONCLUDED);
        assertEquals(ShoppingCartStatus.CONCLUDED, cart.getStatus());
    }
}
