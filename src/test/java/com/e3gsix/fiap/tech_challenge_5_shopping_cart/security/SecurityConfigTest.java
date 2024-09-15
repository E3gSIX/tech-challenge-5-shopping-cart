package com.e3gsix.fiap.tech_challenge_5_shopping_cart.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

public class SecurityConfigTest {

    private final SecurityFilter securityFilter = mock(SecurityFilter.class);
    private final SecurityConfig securityConfig = new SecurityConfig(securityFilter);

    
    @Test
    public void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration configuration = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        when(configuration.getAuthenticationManager()).thenReturn(manager);
        AuthenticationManager result = securityConfig.authenticationManager(configuration);
        assertNotNull(result);
    }
}