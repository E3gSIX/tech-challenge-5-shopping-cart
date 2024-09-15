package com.e3gsix.fiap.tech_challenge_5_shopping_cart.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.e3gsix.fiap.tech_challenge_5_shopping_cart.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SecurityFilterTest {
    private final TokenService tokenService = mock(TokenService.class);
    private final SecurityFilter securityFilter = new SecurityFilter(tokenService);

    @Test
    public void testUnauthorizeResponse() throws IOException {
    	 HttpServletRequest request = mock(HttpServletRequest.class);
    	    HttpServletResponse response = mock(HttpServletResponse.class);
    	    StringWriter stringWriter = new StringWriter();
    	    PrintWriter printWriter = new PrintWriter(stringWriter);

    	    when(request.getRequestURI()).thenReturn("/test");
    	    when(response.getWriter()).thenReturn(printWriter);

    	    securityFilter.unauthorizeResponse(request, response, "Unauthorized");

    	    verify(response, times(1)).setStatus(HttpStatus.UNAUTHORIZED.value());
    	    verify(response, times(1)).setCharacterEncoding("UTF-8");
    	    verify(response, times(1)).setContentType("application/json");

    	    printWriter.flush();
    	    assertNotNull(stringWriter.toString());
    }
}