package com.openclassrooms.starterjwt.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.auth.dto.LoginRequest;
import com.openclassrooms.starterjwt.auth.dto.SignupRequest;
import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController - tests d'intégration")
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/auth/register crée le compte")
    void register_success() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("new@test.com");
        request.setFirstName("Jane");
        request.setLastName("Roe");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @DisplayName("POST /api/auth/register -> 400 si email déjà pris")
    void register_emailAlreadyTaken() throws Exception {
        userRepository.save(new User("taken@test.com", "Roe", "Jane",
                passwordEncoder.encode("password"), false));
        SignupRequest request = new SignupRequest();
        request.setEmail("taken@test.com");
        request.setFirstName("Jane");
        request.setLastName("Roe");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register -> 400 si champ obligatoire manquant")
    void register_missingField() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail(""); // @NotBlank violé
        request.setFirstName("Jane");
        request.setLastName("Roe");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register -> 400 si email invalide")
    void register_invalidEmail() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("not-an-email");
        request.setFirstName("Jane");
        request.setLastName("Roe");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login -> 401 si utilisateur inconnu")
    void login_unknownUser() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@test.com");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login authentifie et renvoie un token")
    void login_success() throws Exception {
        userRepository.save(new User("user@test.com", "Doe", "John",
                passwordEncoder.encode("password"), false));
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("user@test.com"))
                .andExpect(jsonPath("$.admin").value(false));
    }

    @Test
    @DisplayName("POST /api/auth/login -> 401 si mauvais mot de passe")
    void login_badCredentials() throws Exception {
        userRepository.save(new User("user@test.com", "Doe", "John",
                passwordEncoder.encode("password"), false));
        LoginRequest request = new LoginRequest();
        request.setEmail("user@test.com");
        request.setPassword("wrong-password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
