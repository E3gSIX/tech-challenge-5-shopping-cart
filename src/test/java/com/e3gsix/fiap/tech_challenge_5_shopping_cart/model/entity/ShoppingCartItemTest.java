package com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class ShoppingCartItemTest {

	@Test
	public void testSetItemId() {
		ShoppingCartItem item = new ShoppingCartItem();
		item.setItemId(1L);
		assertEquals(1L, item.getItemId());
	}

	@Test
	public void testSetQuantity() {
		ShoppingCartItem item = new ShoppingCartItem();
		item.setQuantity(2);
		assertEquals(2, item.getQuantity());
	}

	@Test
	public void testSetShoppingCart() {
		ShoppingCart cart = new ShoppingCart(UUID.randomUUID());
		ShoppingCartItem item = new ShoppingCartItem();
		item.setShoppingCart(cart);
		assertEquals(cart, item.getShoppingCart());
	}
}