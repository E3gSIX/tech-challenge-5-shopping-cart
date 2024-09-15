package com.e3gsix.fiap.tech_challenge_5_shopping_cart.clients.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import feign.codec.ErrorDecoder;

public class FeignConfigTest {

	@Test
	public void testErrorDecoderBean() {
		FeignConfig feignConfig = new FeignConfig();
		ErrorDecoder errorDecoder = feignConfig.errorDecoder();
		assertNotNull(errorDecoder);
	}

}
