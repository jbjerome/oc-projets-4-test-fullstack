package com.openclassrooms.starterjwt.user.repository;

import com.openclassrooms.starterjwt.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DisplayName("UserRepository - tests d'intégration")
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByEmail retourne l'utilisateur existant")
    void findByEmail_found() {
        userRepository.save(new User("user@test.com", "Doe", "John", "pwd", false));

        assertThat(userRepository.findByEmail("user@test.com")).isPresent();
        assertThat(userRepository.findByEmail("ghost@test.com")).isEmpty();
    }

    @Test
    @DisplayName("existsByEmail reflète la présence de l'utilisateur")
    void existsByEmail() {
        userRepository.save(new User("user@test.com", "Doe", "John", "pwd", false));

        assertThat(userRepository.existsByEmail("user@test.com")).isTrue();
        assertThat(userRepository.existsByEmail("ghost@test.com")).isFalse();
    }
}
