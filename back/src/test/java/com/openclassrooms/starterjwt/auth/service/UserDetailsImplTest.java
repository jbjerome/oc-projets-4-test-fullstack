package com.openclassrooms.starterjwt.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserDetailsImpl - tests unitaires")
class UserDetailsImplTest {

    private UserDetailsImpl build(Long id) {
        return UserDetailsImpl.builder()
                .id(id)
                .username("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("pwd")
                .admin(true)
                .build();
    }

    @Test
    @DisplayName("expose les accesseurs et les drapeaux de compte")
    void gettersAndAccountFlags() {
        UserDetailsImpl details = build(1L);

        assertThat(details.getId()).isEqualTo(1L);
        assertThat(details.getUsername()).isEqualTo("user@test.com");
        assertThat(details.getFirstName()).isEqualTo("John");
        assertThat(details.getLastName()).isEqualTo("Doe");
        assertThat(details.getPassword()).isEqualTo("pwd");
        assertThat(details.getAuthorities()).isEmpty();
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("equals : vrai pour le même id")
    void equals_sameId_true() {
        assertThat(build(1L)).isEqualTo(build(1L));
    }

    @Test
    @DisplayName("equals : faux pour des id différents")
    void equals_differentId_false() {
        assertThat(build(1L)).isNotEqualTo(build(2L));
    }

    @Test
    @DisplayName("equals : faux pour null et pour un autre type, vrai pour soi-même")
    void equals_nullAndOtherType() {
        UserDetailsImpl details = build(1L);
        assertThat(details.equals(details)).isTrue();
        assertThat(details.equals(null)).isFalse();
        assertThat(details.equals("a string")).isFalse();
    }
}
