package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.CredentialsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.ItemsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.NotAuthorizedException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.NotFoundException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ItemResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.UserResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCart;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.ShoppingCartStatus;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.UserRole;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.repository.ShoppingCartRepository;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ShoppingCartServiceImplTest {

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CredentialsClient credentialsClient;

    @Mock
    private ItemsClient itemsClient;

    @Mock
    private TokenService tokenService;

    @Mock
    DecodedJWT decodedJWT;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void add_validData_shouldSaveSuccessfully() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        Integer itemQuantity = 10;
        String token = "token";
        String authorization = "Bearer " + token;
        String username = "username";

        ShoppingCartItemAddRequest request = new ShoppingCartItemAddRequest(itemId, itemQuantity);

        UserResponse userResponse = new UserResponse(userId, username, UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);

        ItemResponse itemResponse = new ItemResponse("name", "description", new BigDecimal("10.00"), itemQuantity * 3);
        when(itemsClient.findById(itemId)).thenReturn(ResponseEntity.ok(itemResponse));

        ShoppingCart shoppingCart = new ShoppingCart(userId);
        when(shoppingCartRepository.findTop1ByStatusAndUserId(ShoppingCartStatus.ACTIVE, userId))
                .thenReturn(Optional.of(shoppingCart));

        Long savedShoppingCartId = 1L;
        ShoppingCart savedShoppingCart = new ShoppingCart(savedShoppingCartId, shoppingCart.getUserId());
        when(shoppingCartRepository.save(shoppingCart)).thenReturn(savedShoppingCart);

        Long result = shoppingCartService.add(authorization, userId, request);

        assertNotNull(result);
        assertEquals(savedShoppingCartId, result);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    void add_lessQuantityItem_shouldThrowUnsupportedOperationException() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        Integer itemQuantity = 10;
        String token = "token";
        String authorization = "Bearer " + token;
        String username = "username";

        ShoppingCartItemAddRequest request = new ShoppingCartItemAddRequest(itemId, itemQuantity);

        UserResponse userResponse = new UserResponse(userId, username, UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);

        Integer lessThanItemQuantity = itemQuantity / 2;
        ItemResponse itemResponse = new ItemResponse("name", "description", new BigDecimal("10.00"), lessThanItemQuantity);
        when(itemsClient.findById(itemId)).thenReturn(ResponseEntity.ok(itemResponse));

        UnsupportedOperationException unsupportedOperationException = assertThrows(
                UnsupportedOperationException.class,
                () -> shoppingCartService.add(authorization, userId, request)
        );

        assertEquals(
                "Item '" + itemResponse.name() + "' está com estoque insuficiente.",
                unsupportedOperationException.getMessage()
        );
    }

    @Test
    void add_userIdNonexistent_shouldThrowNotFoundException() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        Integer itemQuantity = 10;
        String token = "token";
        String authorization = "Bearer " + token;

        ShoppingCartItemAddRequest request = new ShoppingCartItemAddRequest(itemId, itemQuantity);

        when(credentialsClient.findById(userId)).thenReturn(null);

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.add(authorization, userId, request)
        );

        assertEquals("Usuário com id '" + userId + "' não foi encontrado", notFoundException.getMessage());
    }

    @Test
    void add_usernameDifferentThanReceivedFromCredentialsClient_shouldThrowNotAuthorizedException() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        Integer itemQuantity = 10;
        String token = "token";
        String authorization = "Bearer " + token;

        ShoppingCartItemAddRequest request = new ShoppingCartItemAddRequest(itemId, itemQuantity);

        UserResponse userResponse = new UserResponse(userId, "Username", UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn("DifferentUsername");
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);


        NotAuthorizedException notAuthorizedException = assertThrows(
                NotAuthorizedException.class,
                () -> shoppingCartService.add(authorization, userId, request)
        );

        assertEquals("Este usuário não têm permissão para realizar essa ação.", notAuthorizedException.getMessage());
    }


}