package com.openclassrooms.starterjwt.user.service;

import com.openclassrooms.starterjwt.auth.dto.SignupRequest;
import com.openclassrooms.starterjwt.user.exception.EmailAlreadyUsedException;
import com.openclassrooms.starterjwt.user.exception.UnauthorizedDeleteException;
import com.openclassrooms.starterjwt.user.exception.UserNotFoundException;
import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - tests unitaires")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User buildUser() {
        return User.builder()
                .id(1L)
                .email("user@test.com")
                .firstName("John")
                .lastName("Doe")
                .password("encoded")
                .admin(false)
                .build();
    }

    @Test
    @DisplayName("findById retourne l'utilisateur existant")
    void findById_whenExists_returnsUser() {
        User user = buildUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.findById(1L)).isEqualTo(user);
    }

    @Test
    @DisplayName("findById lève UserNotFoundException si absent")
    void findById_whenMissing_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("findByEmail retourne l'utilisateur existant")
    void findByEmail_whenExists_returnsUser() {
        User user = buildUser();
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));

        assertThat(userService.findByEmail("user@test.com")).isEqualTo(user);
    }

    @Test
    @DisplayName("findByEmail lève UserNotFoundException si absent")
    void findByEmail_whenMissing_throws() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("ghost@test.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("delete supprime l'utilisateur quand l'email correspond")
    void delete_whenOwner_deletes() {
        User user = buildUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L, "user@test.com");

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete lève UnauthorizedDeleteException si email différent")
    void delete_whenNotOwner_throws() {
        User user = buildUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.delete(1L, "someone-else@test.com"))
                .isInstanceOf(UnauthorizedDeleteException.class);
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    @DisplayName("delete lève UserNotFoundException si utilisateur absent")
    void delete_whenMissing_throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(1L, "user@test.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("register encode le mot de passe et enregistre l'utilisateur")
    void register_whenEmailFree_savesUser() {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@test.com");
        request.setFirstName("Jane");
        request.setLastName("Roe");
        request.setPassword("password");
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("hashed");
        when(userRepository.save(org.mockito.ArgumentMatchers.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getEmail()).isEqualTo("new@test.com");
        assertThat(saved.getFirstName()).isEqualTo("Jane");
        assertThat(saved.getLastName()).isEqualTo("Roe");
        assertThat(saved.getPassword()).isEqualTo("hashed");
        assertThat(saved.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("register lève EmailAlreadyUsedException si email déjà pris")
    void register_whenEmailTaken_throws() {
        SignupRequest request = new SignupRequest();
        request.setEmail("taken@test.com");
        request.setFirstName("Jane");
        request.setLastName("Roe");
        request.setPassword("password");
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyUsedException.class);
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
