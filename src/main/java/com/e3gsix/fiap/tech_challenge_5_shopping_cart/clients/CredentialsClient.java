package com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients;


import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "credentials-client", url = "${api.client.credentials.url}/users")
public interface CredentialsClient {
    @GetMapping(value = "/{userId}")
    ResponseEntity<UserResponse> findById(@PathVariable UUID userId);
}
