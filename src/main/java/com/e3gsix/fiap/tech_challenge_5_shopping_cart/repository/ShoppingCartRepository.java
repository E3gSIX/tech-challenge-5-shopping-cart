package com.e3gsix.fiap.tech_challenge_5_shopping_cart.repository;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCart;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.ShoppingCartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    Optional<ShoppingCart> findTop1ByStatusAndUserId(ShoppingCartStatus status, UUID userId);
}
