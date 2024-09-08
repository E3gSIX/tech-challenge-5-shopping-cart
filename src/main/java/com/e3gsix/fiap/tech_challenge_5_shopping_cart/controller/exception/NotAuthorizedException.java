package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception;

public class NotAuthorizedException extends RuntimeException {
    public NotAuthorizedException(String message) {
        super(message);
    }
}
