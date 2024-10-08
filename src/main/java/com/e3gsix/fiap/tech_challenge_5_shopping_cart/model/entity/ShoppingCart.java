package com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.ShoppingCartStatus;
import jakarta.persistence.*;

import java.util.*;

@Entity(name = "shopping_carts")
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingCartItem> shoppingCartItems;

    @Enumerated(EnumType.STRING)
    private ShoppingCartStatus status;

    public ShoppingCart() {
    }

    public ShoppingCart(UUID userId) {
        this.setUserId(userId);
        this.shoppingCartItems = new ArrayList<>();
        this.status = ShoppingCartStatus.ACTIVE;
    }

    public ShoppingCart(Long id, UUID userId) {
        this.id = id;
        this.setUserId(userId);
        this.shoppingCartItems = new ArrayList<>();
        this.status = ShoppingCartStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        if (Objects.isNull(userId)) {
            throw new IllegalArgumentException("Id de usuário deve ser informado.");
        }

        this.userId = userId;
    }

    public List<ShoppingCartItem> getShoppingCartItems() {
        return Collections.unmodifiableList(shoppingCartItems);
    }

    public void addItem(ShoppingCartItem shoppingCartItem) {
        Optional<ShoppingCartItem> alreadyInCartItem = this.shoppingCartItems.stream()
                .filter(it -> it.getItemId() == shoppingCartItem.getItemId())
                .findFirst();

        if (alreadyInCartItem.isEmpty()) {
            this.shoppingCartItems.add(shoppingCartItem);
            return;
        }

        ShoppingCartItem itemToUpdate = alreadyInCartItem.get();
        itemToUpdate.setQuantity(itemToUpdate.getQuantity() + shoppingCartItem.getQuantity());
    }

    public void removeItem(Long itemId) {
        this.shoppingCartItems.removeIf(it -> it.getItemId() == itemId);
    }

    public Integer getTotalQuantity() {
        return this.shoppingCartItems.stream()
                .mapToInt(ShoppingCartItem::getQuantity)
                .sum();
    }

    public ShoppingCartStatus getStatus() {
        return status;
    }

    public void setStatus(ShoppingCartStatus status) {
        this.status = status;
    }
}
