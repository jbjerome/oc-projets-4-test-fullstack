package com.openclassrooms.starterjwt.auth.controller;

import com.openclassrooms.starterjwt.auth.dto.JwtResponse;
import com.openclassrooms.starterjwt.auth.dto.LoginRequest;
import com.openclassrooms.starterjwt.auth.dto.SignupRequest;
import com.openclassrooms.starterjwt.auth.service.JwtUtils;
import com.openclassrooms.starterjwt.auth.service.UserDetailsImpl;
import com.openclassrooms.starterjwt.shared.dto.MessageResponse;
import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController - tests unitaires")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("login renvoie un JwtResponse avec le drapeau admin")
    void login_returnsJwtResponse() {
        UserDetailsImpl principal = UserDetailsImpl.builder()
                .id(1L).username("user@test.com").firstName("John").lastName("Doe").build();
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, null);
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");
        User user = User.builder().id(1L).email("user@test.com").firstName("John")
                .lastName("Doe").password("pwd").admin(true).build();
        when(userService.findByEmail("user@test.com")).thenReturn(user);

        ResponseEntity<?> response = authController.authenticateUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(JwtResponse.class);
        JwtResponse body = (JwtResponse) response.getBody();
        assertThat(body.getToken()).isEqualTo("jwt-token");
        assertThat(body.getUsername()).isEqualTo("user@test.com");
        assertThat(body.getAdmin()).isTrue();
    }

    @Test
    @DisplayName("register enregistre l'utilisateur et renvoie un message de succès")
    void register_returnsSuccessMessage() {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@test.com");
        request.setFirstName("Jane");
        request.setLastName("Roe");
        request.setPassword("password");

        ResponseEntity<?> response = authController.registerUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(MessageResponse.class);
        assertThat(((MessageResponse) response.getBody()).getMessage())
                .isEqualTo("User registered successfully!");
        verify(userService).register(request);
    }
}
