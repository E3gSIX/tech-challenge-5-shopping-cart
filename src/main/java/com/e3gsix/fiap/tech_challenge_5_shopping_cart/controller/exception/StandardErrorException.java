package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception;

public class StandardErrorException extends RuntimeException {

	private static final long serialVersionUID = -2490541313276008223L;
	private final StandardError standardError;

    public StandardErrorException(StandardError standardError) {
        this.standardError = standardError;
    }

    public StandardError getStandardError() {
        return standardError;
    }
}
