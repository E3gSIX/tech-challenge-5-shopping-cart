package com.e3gsix.fiap.tech_challenge_5_shopping_cart;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TechChallenge5ShoppingCartApplicationTests {

	@Test
	public void contextLoads() {
		assertDoesNotThrow(() -> TechChallenge5ShoppingCartApplication.main(new String[] {}));
	}

}
