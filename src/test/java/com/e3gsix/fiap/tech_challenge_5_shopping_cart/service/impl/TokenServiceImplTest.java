package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums.UserRole;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {
    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private DecodedJWT jwt;

    @Mock
    private Claim claim;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(tokenService, "secret", "test-secret");
    }

    @Test
    public void validateToken_validToken_shouldReturnValidDecodedJWT() {
        String token = JWT.create()
                .withIssuer("auth-api")
                .withSubject("testUser")
                .sign(Algorithm.HMAC256("test-secret"));

        DecodedJWT decodedJWT = tokenService.validateToken(token);

        assertNotNull(decodedJWT);
        assertEquals("auth-api", decodedJWT.getIssuer());
        assertEquals("testUser", decodedJWT.getSubject());
    }

    @Test
    public void validateToken_invalidToken_shouldReturnNull() {
        String token = "invalid-token";

        DecodedJWT decodedJWT = tokenService.validateToken(token);

        assertNull(decodedJWT);
    }

    @Test
    public void getAuthoritiesFromToken_withAuthorities_shouldReturnAllAuthorities() {
        String jsonString = "[\"ROLE_USER\", \"ROLE_ADMIN\"]";

        when(claim.toString()).thenReturn(jsonString);
        when(jwt.getClaim("authorities")).thenReturn(claim);

        Collection<? extends GrantedAuthority> authorities = tokenService.getAuthoritiesFromToken(jwt);

        assertNotNull(authorities);
        assertEquals(2, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void getAuthoritiesFromToken_invalidAuthorities_shouldReturnNull() {
        when(claim.toString()).thenReturn("{invalid-json}");
        when(jwt.getClaim("authorities")).thenReturn(claim);

        Collection<? extends GrantedAuthority> authorities = tokenService.getAuthoritiesFromToken(jwt);

        assertNull(authorities);
    }

    @Test
    public void recoverToken_bearerToken_shouldReturnJustToken() {
        String token = "token";
        String authorization = "Bearer " + token;

        assertEquals(token, tokenService.recoverToken(authorization));
    }

    @Test
    public void isAdmin_withAdminAuthorities_shouldReturnTrue() {
        String token = "testing.token";
        List authorities = Collections.singletonList(new SimpleGrantedAuthority(UserRole.ADMIN.getRole()));

        TokenService tokenServiceSpy = spy(new TokenServiceImpl());
        doReturn(jwt).when(tokenServiceSpy).validateToken(token);
        doReturn(authorities).when(tokenServiceSpy).getAuthoritiesFromToken(jwt);

        assertTrue(tokenServiceSpy.isAdmin(token));
    }

    @Test
    public void isAdmin_withoutAdminAuthorities_shouldReturnFalse() {
        String token = "testing.token";
        List authorities = Collections.singletonList(new SimpleGrantedAuthority("USER"));

        TokenService tokenServiceSpy = spy(new TokenServiceImpl());

        doReturn(jwt).when(tokenServiceSpy).validateToken(token);
        doReturn(authorities).when(tokenServiceSpy).getAuthoritiesFromToken(jwt);

        assertFalse(tokenServiceSpy.isAdmin(token));
    }
}