package com.e3gsix.fiap.tech_challenge_5_shopping_cart.controller.exception;

public class NotFoundException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
        super(message);
    }
}
