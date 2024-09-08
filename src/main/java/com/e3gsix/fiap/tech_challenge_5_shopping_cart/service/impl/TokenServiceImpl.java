package com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.TokenService;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String recoverToken(String authorizationHeader) {
        return authorizationHeader.replace("Bearer ", "");
    }

    @Override
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(DecodedJWT jwt) {
        try {
            String jsonString = jwt.getClaim("authorities").toString();
            return new JSONParser(jsonString).list().stream()
                    .map(String::valueOf)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
