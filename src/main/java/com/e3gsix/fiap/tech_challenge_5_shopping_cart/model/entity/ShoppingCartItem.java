package com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity;

import jakarta.persistence.*;

@Entity(name = "shopping_cart_items")
public class ShoppingCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long itemId;
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    public ShoppingCartItem() {
    }

    public ShoppingCartItem(Long itemId, Integer quantity) {
        this.setItemId(itemId);
        this.setQuantity(quantity);
    }

    public ShoppingCartItem(Long itemId, Integer quantity, ShoppingCart shoppingCart) {
        this.setItemId(itemId);
        this.setQuantity(quantity);
        this.shoppingCart = shoppingCart;
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        if (itemId < 0) {
            throw new IllegalArgumentException("Id do item não pode estar negativo no carrinho.");
        }

        this.itemId = itemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantidade do item não pode estar negativa no carrinho.");
        }

        this.quantity = quantity;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}
