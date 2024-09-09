package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.impl;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.ShoppingCartController;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ShoppingCartResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.ShoppingCartService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    public static final String URL_SHOPPING_CART_ITEM = "/items";
    public static final String URL_SHOPPING_CART_BY_ID = "/{id}";

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartControllerImpl(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    @PostMapping(URL_SHOPPING_CART_ITEM)
    public ResponseEntity add(
            @RequestHeader("Authorization") String authorization,
            @RequestParam UUID userId,
            @RequestBody ShoppingCartItemAddRequest request,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Long activeShoppingCartId = this.shoppingCartService.add(authorization, userId, request);

        URI uri = uriComponentsBuilder.path(URL_SHOPPING_CART.concat(URL_SHOPPING_CART_BY_ID))
                .buildAndExpand(activeShoppingCartId)
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @Override
    @DeleteMapping(URL_SHOPPING_CART_ITEM)
    public ResponseEntity remove(
            @RequestHeader("Authorization") String authorization,
            @RequestParam UUID userId,
            @RequestParam Long itemId,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        Long activeShoppingCartId = this.shoppingCartService.remove(authorization, userId, itemId);

        URI uri = uriComponentsBuilder.path(URL_SHOPPING_CART.concat(URL_SHOPPING_CART_BY_ID))
                .buildAndExpand(activeShoppingCartId)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uri);

        return new ResponseEntity(headers, HttpStatus.NO_CONTENT);
    }

    @Override
    @GetMapping(URL_SHOPPING_CART_BY_ID)
    public ResponseEntity<ShoppingCartResponse> findById(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestParam UUID userId
    ) {
        ShoppingCartResponse response = this.shoppingCartService.findById(authorization, userId, id);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity conclude(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestParam UUID userId
    ) {
        this.shoppingCartService.conclude(authorization, userId, id);

        return ResponseEntity.ok().build();
    }
}
