package com.e3gsix.fiap.tech_challenge_5_shopping_cart;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TechChallenge5ShoppingCartApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    void main() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(
                    () -> SpringApplication.run(TechChallenge5ShoppingCartApplication.class, new String[]{})
            ).thenReturn(null);

            TechChallenge5ShoppingCartApplication.main(new String[]{});

            mockedSpringApplication.verify(
                    () -> SpringApplication.run(TechChallenge5ShoppingCartApplication.class, new String[]{})
            );
        }
    }

}
