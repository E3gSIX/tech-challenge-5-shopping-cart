package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.impl;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.CredentialsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.ItemsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.NotAuthorizedException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.NotFoundException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ItemResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.UserResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCart;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCartItem;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.ShoppingCartStatus;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.repository.ShoppingCartRepository;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.ShoppingCartService;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CredentialsClient credentialsClient;
    private final ItemsClient itemsClient;
    private final TokenService tokenService;

    public ShoppingCartServiceImpl(
            ShoppingCartRepository shoppingCartRepository,
            CredentialsClient credentialsClient,
            ItemsClient itemsClient,
            TokenService tokenService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.credentialsClient = credentialsClient;
        this.itemsClient = itemsClient;
        this.tokenService = tokenService;
    }

    @Override
    public Long add(String authorization, UUID userId, ShoppingCartItemAddRequest request) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(request.itemId(), request.quantity());

        UserResponse userFound = getValidatedUser(userId);

        validatePermission(authorization, userFound);

        ItemResponse itemFound = getValidatedItem(request.itemId());
        if (request.quantity() > itemFound.quantity()) {
            throw new UnsupportedOperationException("Item '" + itemFound.name() + "' está com estoque insuficiente.");
        }

        ShoppingCart activeShoppingCart = this.getActiveShoppingCart(userFound.id());
        shoppingCartItem.setShoppingCart(activeShoppingCart);
        activeShoppingCart.addItem(shoppingCartItem);

        ShoppingCart savedShoppingCart = this.shoppingCartRepository.save(activeShoppingCart);

        return savedShoppingCart.getId();
    }

    private UserResponse getValidatedUser(UUID userId) {
        ResponseEntity<UserResponse> userResponse = this.credentialsClient.findById(userId);
        if (Objects.isNull(userResponse) ||
                userResponse.getStatusCode() != HttpStatus.OK ||
                Objects.isNull(userResponse.getBody())
        ) {
            throw new NotFoundException("Usuário com id '" + userId + "' não foi encontrado");
        }

        return userResponse.getBody();
    }

    private void validatePermission(String authorization, UserResponse userFound) {
        String usernameToken = getUsernameFromAuthorization(authorization);

        boolean isInsecureDirectObjectReferenceVulnerability = !usernameToken.equals(userFound.username());
        if (isInsecureDirectObjectReferenceVulnerability) {
            throw new NotAuthorizedException("Este usuário não têm permissão para realizar essa ação.");
        }
    }

    private String getUsernameFromAuthorization(String authorization) {
        String token = this.tokenService.recoverToken(authorization);
        return this.tokenService.validateToken(token).getSubject();
    }

    private ItemResponse getValidatedItem(Long itemId) {
        ResponseEntity<ItemResponse> itemResponse = this.itemsClient.findById(itemId);
        if (Objects.isNull(itemResponse) ||
                itemResponse.getStatusCode() != HttpStatus.OK ||
                Objects.isNull(itemResponse.getBody())
        ) {
            throw new NotFoundException("Item com id '" + itemId + "' não foi encontrado");
        }

        return itemResponse.getBody();
    }

    private ShoppingCart getActiveShoppingCart(UUID userId) {
        return this.shoppingCartRepository.findTop1ByStatusAndUserId(ShoppingCartStatus.ACTIVE, userId)
                .orElse(new ShoppingCart(userId));
    }
}
