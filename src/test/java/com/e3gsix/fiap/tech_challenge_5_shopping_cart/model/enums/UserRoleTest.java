package com.e3gsix.fiap.tech_challenge_5_shopping_cart.model.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserRoleTest {

    @Test
    void getRole_withValue_successful() {
        assertEquals("ADMIN", UserRole.ADMIN.getRole());
        assertEquals("USER", UserRole.USER.getRole());
    }

    @Test
    void getRole_withWrongValue_successful() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> UserRole.valueOf("INVALID_ROLE"));
    }
}
