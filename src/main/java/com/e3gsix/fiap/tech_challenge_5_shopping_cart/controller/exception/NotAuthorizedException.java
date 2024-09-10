package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception;

public class NotAuthorizedException extends RuntimeException {

	private static final long serialVersionUID = -3828169832511013192L;

	public NotAuthorizedException(String message) {
        super(message);
    }
}
