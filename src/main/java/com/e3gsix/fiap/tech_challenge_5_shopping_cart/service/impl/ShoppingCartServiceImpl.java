package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.impl;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.CredentialsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.NotFoundException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.UserResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCart;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCartItem;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.ShoppingCartStatus;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.repository.ShoppingCartRepository;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CredentialsClient credentialsClient;

    public ShoppingCartServiceImpl(
            ShoppingCartRepository shoppingCartRepository,
            CredentialsClient credentialsClient
    ) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.credentialsClient = credentialsClient;
    }

    @Override
    public Long add(UUID userId, ShoppingCartItemAddRequest request) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(request.itemId(), request.quantity());

        ResponseEntity<UserResponse> userFound = this.credentialsClient.findById(userId);
        if (Objects.isNull(userFound)) {
            throw new NotFoundException("Usuário com id '" + userId + "' não foi encontrado");
        }

        ShoppingCart activeShoppingCart = this.getActiveShoppingCart(userId);
        shoppingCartItem.setShoppingCart(activeShoppingCart);
        activeShoppingCart.addItem(shoppingCartItem);

        ShoppingCart savedShoppingCart = this.shoppingCartRepository.save(activeShoppingCart);

        return savedShoppingCart.getId();
    }

    private ShoppingCart getActiveShoppingCart(UUID userId) {
        return this.shoppingCartRepository.findTop1ByStatusAndUserId(ShoppingCartStatus.ACTIVE, userId)
                .orElse(new ShoppingCart(userId));
    }
}
