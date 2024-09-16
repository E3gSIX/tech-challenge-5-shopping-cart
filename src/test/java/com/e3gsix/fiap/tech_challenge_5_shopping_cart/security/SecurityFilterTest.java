package com.e3gsix.fiap.tech_challenge_5_shopping_cart.security;

import static com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.impl.ShoppingCartControllerImpl.URL_SHOPPING_CART;
import static com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.impl.ShoppingCartControllerImpl.URL_SHOPPING_CART_ITEM;
import static com.e3gsix.fiap.tech_challenge_5_shopping_cart.swagger.SwaggerConfig.URL_SWAGGER;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class SecurityFilterTest {
	@Mock
	private TokenService tokenService;

	@InjectMocks
	private SecurityFilter securityFilter;

	@Mock
	private FilterChain filterChain;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@BeforeEach
	public void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void doFilterInternal_permittedEndpoint_proceedWithNoAuthentication() throws ServletException, IOException {
		request.setRequestURI(URL_SWAGGER);
		request.setMethod(HttpMethod.GET.name());

		securityFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(request, response);
		assertFalse(response.getContentAsString().contains("Token não foi encontrado."));
		assertFalse(response.getContentAsString().contains("Token recebido não é válido."));
	}

	@Test
	public void doFilterInternal_permittedUrlButWrongHttpMethod_shouldUnauthorized() throws Exception {
		request.setRequestURI(URL_SWAGGER);
		request.setMethod(HttpMethod.POST.name());

		securityFilter.doFilterInternal(request, response, filterChain);

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
		assertTrue(response.getContentAsString().contains("Token não foi encontrado."));
	}

	@Test
	public void doFilterInternal_noToken_shouldReturnUnauthorized() throws ServletException, IOException {
		request.setRequestURI(URL_SHOPPING_CART.concat(URL_SHOPPING_CART_ITEM));
		request.setMethod(HttpMethod.POST.name());

		securityFilter.doFilterInternal(request, response, filterChain);

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
		assertTrue(response.getContentAsString().contains("Token não foi encontrado."));
	}

	@Test
	public void doFilterInternal_invalidToken_shouldReturnUnauthorized() throws ServletException, IOException {
		request.setRequestURI(URL_SHOPPING_CART.concat(URL_SHOPPING_CART_ITEM));
		request.setMethod(HttpMethod.POST.name());
		request.addHeader("Authorization", "Bearer invalid-token");

		when(tokenService.recoverToken(anyString())).thenReturn("Test token");
		when(tokenService.validateToken(anyString())).thenReturn(null);

		securityFilter.doFilterInternal(request, response, filterChain);

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
		assertTrue(response.getContentAsString().contains("Token recebido não é válido."));
	}

	@Test
	public void doFilterInternal_validToken_shouldAuthenticateAndProceed() throws ServletException, IOException {
		request.setRequestURI(URL_SHOPPING_CART.concat(URL_SHOPPING_CART_ITEM));
		request.setMethod(HttpMethod.POST.name());
		request.addHeader("Authorization", "Bearer valid-token");

		when(tokenService.recoverToken(anyString())).thenReturn("Test token");

		DecodedJWT decodedJWT = mock(DecodedJWT.class);
		when(tokenService.validateToken(anyString())).thenReturn(decodedJWT);
		when(tokenService.getAuthoritiesFromToken(decodedJWT)).thenReturn(List.of());

		securityFilter.doFilterInternal(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		verify(filterChain, times(1)).doFilter(request, response);
	}
}