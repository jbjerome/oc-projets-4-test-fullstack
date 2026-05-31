package com.openclassrooms.starterjwt.user.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User (modèle) - tests unitaires")
class UserModelTest {

    @Test
    @DisplayName("builder, constructeur complet et accesseurs chaînés")
    void builderAllArgsAndAccessors() {
        LocalDateTime now = LocalDateTime.now();
        User built = User.builder()
                .id(1L).email("user@test.com").firstName("John").lastName("Doe")
                .password("pwd").admin(true).createdAt(now).updatedAt(now).build();

        assertThat(built.getId()).isEqualTo(1L);
        assertThat(built.getEmail()).isEqualTo("user@test.com");
        assertThat(built.getFirstName()).isEqualTo("John");
        assertThat(built.getLastName()).isEqualTo("Doe");
        assertThat(built.getPassword()).isEqualTo("pwd");
        assertThat(built.isAdmin()).isTrue();
        assertThat(built.getCreatedAt()).isEqualTo(now);
        assertThat(built.getUpdatedAt()).isEqualTo(now);
        assertThat(User.builder().toString()).isNotNull();

        User full = new User(2L, "a@b.com", "Last", "First", "secret", false, now, now);
        assertThat(full.getId()).isEqualTo(2L);

        User viaSetters = new User()
                .setId(3L).setEmail("c@d.com").setFirstName("F").setLastName("L")
                .setPassword("p").setAdmin(true).setCreatedAt(now).setUpdatedAt(now);
        assertThat(viaSetters.getId()).isEqualTo(3L);
        assertThat(viaSetters.getEmail()).isEqualTo("c@d.com");
        assertThat(viaSetters.isAdmin()).isTrue();
        assertThat(viaSetters.toString()).contains("c@d.com");
    }

    @Test
    @DisplayName("constructeur requis (champs @NonNull)")
    void requiredArgsConstructor() {
        User user = new User("user@test.com", "Doe", "John", "pwd", false);

        assertThat(user.getEmail()).isEqualTo("user@test.com");
        assertThat(user.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("equals/hashCode basés sur l'id, avec cas limites")
    void equalsAndHashCode() {
        User a = User.builder().id(1L).email("a@test.com").firstName("A").lastName("A")
                .password("p").admin(false).build();
        User sameId = User.builder().id(1L).email("b@test.com").firstName("B").lastName("B")
                .password("p").admin(true).build();
        User otherId = User.builder().id(2L).email("a@test.com").firstName("A").lastName("A")
                .password("p").admin(false).build();

        assertThat(a).isEqualTo(a);
        assertThat(a).isEqualTo(sameId).hasSameHashCodeAs(sameId);
        assertThat(a).isNotEqualTo(otherId);
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("a string");

        User nullId1 = User.builder().email("x@test.com").firstName("X").lastName("X").password("p").admin(false).build();
        User nullId2 = User.builder().email("y@test.com").firstName("Y").lastName("Y").password("p").admin(false).build();
        assertThat(nullId1).isEqualTo(nullId2);
        assertThat(nullId1).isNotEqualTo(a);
    }
}
