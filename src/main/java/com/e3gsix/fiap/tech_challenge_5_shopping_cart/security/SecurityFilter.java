package com.e3gsix.fiap.tech_challenge_5_shopping_cart.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception.StandardError;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.utils.JsonUtil;
import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static com.e3gsix.fiap.tech_challenge_5_shopping_cart.swagger.SwaggerConfig.*;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    private final Map<String, HttpMethod> PERMITTED_RESOURCE = Map.of(
            URL_SWAGGER, HttpMethod.GET,
            URL_SWAGGER_DEFAULT, HttpMethod.GET,
            URL_SWAGGER_API, HttpMethod.GET
    );

    public SecurityFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (isPermittedEndpoint(request.getRequestURI(), request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = this.recoverToken(request);

        if (Objects.isNull(token)) {
            unauthorizeResponse(request, response, "Token não foi encontrado.");
            return;
        }

        DecodedJWT decodedJWT = this.tokenService.validateToken(token);
        var authorities = this.tokenService.getAuthoritiesFromToken(decodedJWT);
        var authentication = new UsernamePasswordAuthenticationToken(decodedJWT, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (Objects.isNull(decodedJWT)) {
            unauthorizeResponse(request, response, "Token recebido não é válido.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPermittedEndpoint(String endpoint, String method) {
        for (String resourceEndpoint : PERMITTED_RESOURCE.keySet()) {
            if (endpoint.contains(resourceEndpoint)) {
                HttpMethod resourceMethod = PERMITTED_RESOURCE.get(resourceEndpoint);
                if (method.equals(resourceMethod.name())) return true;
            }
        }

        return false;
    }

    private void unauthorizeResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String message
    ) throws IOException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        StandardError error = StandardError.create(status, message, request.getRequestURI());

        String jsonResponse = JsonUtil.createObjectMapper().writeValueAsString(error);

        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
        response.setContentType("application/json");
    }

    private String recoverToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) return null;
        return this.tokenService.recoverToken(authorization);
    }
}
