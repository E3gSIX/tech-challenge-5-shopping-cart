package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.impl;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.CredentialsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.ItemsClient;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.NotAuthorizedException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.NotFoundException;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.request.ShoppingCartItemAddRequest;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ItemResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.PaymentIntegrityResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ShoppingCartResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.UserResponse;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCart;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.entity.ShoppingCartItem;
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

    @Test
    void remove_validData_shouldRemoveItemFromShoppingCartSuccessfully() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        Integer itemQuantity = 10;
        String token = "token";
        String authorization = "Bearer " + token;
        String username = "username";

        UserResponse userResponse = new UserResponse(userId, username, UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);

        ItemResponse itemResponse = new ItemResponse("name", "description", new BigDecimal("10.00"), itemQuantity * 3);
        when(itemsClient.findById(itemId)).thenReturn(ResponseEntity.ok(itemResponse));

        Long shoppingCartId = 123L;
        ShoppingCart shoppingCart = new ShoppingCart(shoppingCartId, userId);
        shoppingCart.addItem(new ShoppingCartItem(itemId, 10));

        when(shoppingCartRepository.findTop1ByStatusAndUserId(ShoppingCartStatus.ACTIVE, userId))
                .thenReturn(Optional.of(shoppingCart));

        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);

        Long result = shoppingCartService.remove(authorization, userId, itemId);

        assertNotNull(result);
        assertEquals(shoppingCartId, result);
        assertEquals(0, shoppingCart.getShoppingCartItems().size());
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
    }

    @Test
    void findById_validData_shouldReturnShoppingCartSuccessfully() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        Integer itemQuantity = 10;
        String token = "token";
        String authorization = "Bearer " + token;
        String username = "username";

        UserResponse userResponse = new UserResponse(userId, username, UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);

        Long shoppingCartId = 123L;
        ShoppingCart shoppingCart = new ShoppingCart(shoppingCartId, userId);
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(1L, itemId, 10, shoppingCart);
        shoppingCart.addItem(shoppingCartItem);
        when(shoppingCartRepository.findById(itemId)).thenReturn(Optional.of(shoppingCart));

        ItemResponse itemResponse = new ItemResponse("name", "description", new BigDecimal("10.00"), itemQuantity * 3);
        when(itemsClient.findById(itemId)).thenReturn(ResponseEntity.ok(itemResponse));

        ShoppingCartResponse result = shoppingCartService.findById(itemId);

        assertNotNull(result);
        assertEquals(shoppingCartId, result.id());
        assertEquals(userId, result.userId());

        BigDecimal expectedTotal = itemResponse.price().multiply(new BigDecimal(shoppingCartItem.getQuantity()));
        assertEquals(expectedTotal, result.total());

        assertEquals(ShoppingCartStatus.ACTIVE, result.status());

        verify(shoppingCartRepository, times(1)).findById(itemId);
        verify(itemsClient, times(1)).findById(itemId);
    }

    @Test
    void findById_shoppingCartNonexistent_shouldThrowNotFoundException() {
        Long shoppingCartId = 123L;

        when(shoppingCartRepository.findById(shoppingCartId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.findById(shoppingCartId)
        );

        assertEquals(
                "Carrinho de compras com id '" + shoppingCartId + "' não foi encontrado",
                notFoundException.getMessage()
        );
    }

    @Test
    void checkPaymentIntegrity_validActiveShoppingCart_shouldReturnTrueIntegrity() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        String token = "token";
        String authorization = "Bearer " + token;

        Long shoppingCartId = 123L;
        ShoppingCart shoppingCart = new ShoppingCart(shoppingCartId, userId);
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(1L, itemId, 10, shoppingCart);
        shoppingCart.addItem(shoppingCartItem);
        when(shoppingCartRepository.findById(itemId)).thenReturn(Optional.of(shoppingCart));

        PaymentIntegrityResponse result = shoppingCartService.checkPaymentIntegrity(authorization, itemId);

        assertNotNull(result);
        assertTrue(result.integrity());
        assertEquals("Ready to conclude.", result.reason());
    }

    @Test
    void checkPaymentIntegrity_emptyActiveShoppingCart_shouldThrowUnsupportedOperationException() {
        UUID userId = UUID.randomUUID();
        Long itemId = 1L;
        String token = "token";
        String authorization = "Bearer " + token;

        Long shoppingCartId = 123L;
        ShoppingCart shoppingCart = new ShoppingCart(shoppingCartId, userId);
        when(shoppingCartRepository.findById(itemId)).thenReturn(Optional.of(shoppingCart));

        PaymentIntegrityResponse result = shoppingCartService.checkPaymentIntegrity(authorization, itemId);

        assertNotNull(result);
        assertFalse(result.integrity());
        assertEquals("O carrinho precisa ter ao menos um item para ser concluído.", result.reason());
    }

    @Test
    void conclude_userIdNonexistent_shouldThrowNotFoundException() {
        UUID userId = UUID.randomUUID();
        Long shoppingCartId = 123L;
        String token = "token";
        String authorization = "Bearer " + token;

        when(credentialsClient.findById(userId)).thenReturn(null);

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.conclude(authorization, userId, shoppingCartId)
        );

        assertEquals("Usuário com id '" + userId + "' não foi encontrado", notFoundException.getMessage());
    }

    @Test
    void conclude_usernameDifferentThanReceivedFromCredentialsClient_shouldThrowNotAuthorizedException() {
        UUID userId = UUID.randomUUID();
        Long shoppingCartId = 123L;
        String username = "username";
        String token = "token";
        String authorization = "Bearer " + token;

        UserResponse userResponse = new UserResponse(userId, username, UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn("DifferentThanUsername");
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);

        NotAuthorizedException notAuthorizedException = assertThrows(
                NotAuthorizedException.class,
                () -> shoppingCartService.conclude(authorization, userId, shoppingCartId)
        );

        assertEquals("Este usuário não têm permissão para realizar essa ação.", notAuthorizedException.getMessage());
    }

    @Test
    void conclude_shoppingCartNonexistent_shouldThrowNotFoundException() {
        UUID userId = UUID.randomUUID();
        Long shoppingCartId = 123L;
        String username = "username";
        String token = "token";
        String authorization = "Bearer " + token;

        UserResponse userResponse = new UserResponse(userId, username, UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);

        when(shoppingCartRepository.findById(shoppingCartId)).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> shoppingCartService.conclude(authorization, userId, shoppingCartId)
        );

        assertEquals(
                "Carrinho de compras com id '" + shoppingCartId + "' não foi encontrado",
                notFoundException.getMessage()
        );
    }

    @Test
    void conclude_shoppingCartFinalState_shouldThrowUnsupportedOperationException() {
        UUID userId = UUID.randomUUID();
        Long shoppingCartId = 123L;
        String username = "username";
        String token = "token";
        String authorization = "Bearer " + token;

        UserResponse userResponse = new UserResponse(userId, username, UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);

        ShoppingCart shoppingCart = new ShoppingCart(shoppingCartId, userId);
        shoppingCart.setStatus(ShoppingCartStatus.CONCLUDED); // final state
        when(shoppingCartRepository.findById(shoppingCartId)).thenReturn(Optional.of(shoppingCart));

        UnsupportedOperationException unsupportedOperationException = assertThrows(
                UnsupportedOperationException.class,
                () -> shoppingCartService.conclude(authorization, userId, shoppingCartId)
        );

        assertEquals(
                "O carrinho já se encontra em estado final.",
                unsupportedOperationException.getMessage()
        );
    }

    @Test
    void conclude_validData_shouldConcludeShoppingCartSuccessfully() {
        UUID userId = UUID.randomUUID();
        Long shoppingCartId = 123L;
        String username = "username";
        String token = "token";
        String authorization = "Bearer " + token;

        UserResponse userResponse = new UserResponse(userId, username, UserRole.ADMIN);
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        when(tokenService.recoverToken(authorization)).thenReturn(token);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(tokenService.validateToken(token)).thenReturn(decodedJWT);

        ShoppingCart shoppingCart = new ShoppingCart(shoppingCartId, userId);
        when(shoppingCartRepository.findById(shoppingCartId)).thenReturn(Optional.of(shoppingCart));

        shoppingCartService.conclude(authorization, userId, shoppingCartId);
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        assertEquals(ShoppingCartStatus.CONCLUDED, shoppingCart.getStatus());
    }
}