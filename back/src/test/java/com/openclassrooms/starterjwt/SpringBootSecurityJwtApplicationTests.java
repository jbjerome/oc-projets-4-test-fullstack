package com.openclassrooms.starterjwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Contexte Spring - test d'intégration")
class SpringBootSecurityJwtApplicationTests {

    @Test
    @DisplayName("le contexte applicatif se charge (config sécurité, beans)")
    void contextLoads() {
        // Vérifie que WebSecurityConfig, AppConfig et l'ensemble des beans se chargent.
    }
}
