package com.e3gsix.fiap.tech_challenge_5_shopping_cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TechChallenge5ShoppingCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechChallenge5ShoppingCartApplication.class, args);
	}

}
