package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface TokenService {

    String recoverToken(String authorization);

    DecodedJWT validateToken(String token);

    Collection<? extends GrantedAuthority> getAuthoritiesFromToken(DecodedJWT jwt);
}
