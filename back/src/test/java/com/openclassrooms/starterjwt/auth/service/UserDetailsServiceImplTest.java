package com.openclassrooms.starterjwt.auth.service;

import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl - tests unitaires")
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    @DisplayName("loadUserByUsername retourne les UserDetails de l'utilisateur trouvé")
    void loadUserByUsername_whenFound_returnsDetails() {
        User user = User.builder()
                .id(1L).email("user@test.com").firstName("John").lastName("Doe")
                .password("pwd").admin(false).build();
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("user@test.com");

        assertThat(result.getUsername()).isEqualTo("user@test.com");
        assertThat(result.getPassword()).isEqualTo("pwd");
    }

    @Test
    @DisplayName("loadUserByUsername lève UsernameNotFoundException si absent")
    void loadUserByUsername_whenMissing_throws() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("ghost@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
