package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

public class AdviceExceptionHandlerTest {

    private final AdviceExceptionHandler handler = new AdviceExceptionHandler();
    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    public void testHandleIllegalArgumentException() {
        when(request.getRequestURI()).thenReturn("/test");
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        ResponseEntity<StandardError> response = handler.handleIllegalArgumentException(exception, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", response.getBody().message());
    }

    @Test
    public void testHandleNotFoundException() {
        when(request.getRequestURI()).thenReturn("/test");
        NotFoundException exception = new NotFoundException("Not found");
        ResponseEntity<StandardError> response = handler.handleNotFoundException(exception, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().message());
    }

    @Test
    public void testHandleNotAuthorizedException() {
        when(request.getRequestURI()).thenReturn("/test");
        NotAuthorizedException exception = new NotAuthorizedException("Not authorized");
        ResponseEntity<StandardError> response = handler.handleNotAuthorizedException(exception, request);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Not authorized", response.getBody().message());
    }

    @Test
    public void testHandleStandardErrorException() {
        when(request.getRequestURI()).thenReturn("/test");
        StandardError standardError = StandardError.create(HttpStatus.BAD_REQUEST, "Error", "/test");
        StandardErrorException exception = new StandardErrorException(standardError);
        ResponseEntity<StandardError> response = handler.handleStandardErrorException(exception, request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error", response.getBody().message());
    }

    @Test
    public void testHandleGenericException() {
        when(request.getRequestURI()).thenReturn("/test");
        Exception exception = new Exception("Generic error");
        ResponseEntity<StandardError> response = handler.handleGenericException(exception, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Generic error", response.getBody().message());
    }
}