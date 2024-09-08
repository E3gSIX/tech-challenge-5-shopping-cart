package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.impl;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.ShoppingCartController;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.ShoppingCartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.impl.ShoppingCartControllerImpl.URL_SHOPPING_CART;

@RestController
@RequestMapping(URL_SHOPPING_CART)
public class ShoppingCartControllerImpl implements ShoppingCartController {

    public static final String URL_SHOPPING_CART = "/shopping-cart";
    public static final String URL_SHOPPING_CART_BY_USERID = "/{userId}";
    public static final String URL_SHOPPING_CART_BY_ID = "/{id}";

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartControllerImpl(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    @PostMapping(URL_SHOPPING_CART_BY_USERID)
    public ResponseEntity add(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID userId,
            @RequestBody ShoppingCartItemAddRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Long activeShoppingCartId = this.shoppingCartService.add(authorization, userId, request);

        URI uri = uriComponentsBuilder.path(URL_SHOPPING_CART.concat(URL_SHOPPING_CART_BY_ID))
                .buildAndExpand(activeShoppingCartId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }
}
