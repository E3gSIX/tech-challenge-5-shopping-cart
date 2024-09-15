package com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.ItemResponse;

public class ItemsClientTest {

    @Mock
    private CredentialsClient credentialsClient;

    @Mock
    private ItemsClient itemsClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindItemById() {
        Long itemId = 1L;
        ItemResponse itemResponse = new ItemResponse(null, null, null, null);
        ResponseEntity<ItemResponse> responseEntity = new ResponseEntity<>(itemResponse, HttpStatus.OK);

        when(itemsClient.findById(itemId)).thenReturn(responseEntity);

        ResponseEntity<ItemResponse> response = itemsClient.findById(itemId);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }
}