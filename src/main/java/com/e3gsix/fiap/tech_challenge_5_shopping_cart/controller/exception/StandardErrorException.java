package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception;

public class StandardErrorException extends RuntimeException {

    private final StandardError standardError;

    public StandardErrorException(StandardError standardError) {
        this.standardError = standardError;
    }

    public StandardError getStandardError() {
        return standardError;
    }
}
