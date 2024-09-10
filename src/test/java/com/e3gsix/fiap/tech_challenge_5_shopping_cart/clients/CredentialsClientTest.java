package com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.UserResponse;

public class CredentialsClientTest {

    @Mock
    private CredentialsClient credentialsClient;

    @InjectMocks
    private CredentialsClientTest credentialsClientTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindById() {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(userId, null, null);
        // Set up the mock response
        when(credentialsClient.findById(userId)).thenReturn(ResponseEntity.ok(userResponse));

        ResponseEntity<UserResponse> response = credentialsClient.findById(userId);
        assertNotNull(response);
    }
}