package com.openclassrooms.starterjwt.user.controller;

import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("UserController - tests d'intégration")
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/user/{id} renvoie l'utilisateur")
    void findById_found() throws Exception {
        User user = userRepository.save(new User("user@test.com", "Doe", "John", "pwd", false));
        mockMvc.perform(get("/api/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/user/{id} -> 404 si absent")
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/api/user/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "owner@test.com")
    @DisplayName("DELETE /api/user/{id} supprime son propre compte")
    void delete_owner_success() throws Exception {
        User user = userRepository.save(new User("owner@test.com", "Doe", "John", "pwd", false));
        mockMvc.perform(delete("/api/user/" + user.getId()))
                .andExpect(status().isOk());
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    @WithMockUser(username = "intruder@test.com")
    @DisplayName("DELETE /api/user/{id} -> 401 si ce n'est pas son compte")
    void delete_notOwner_unauthorized() throws Exception {
        User user = userRepository.save(new User("owner@test.com", "Doe", "John", "pwd", false));
        mockMvc.perform(delete("/api/user/" + user.getId()))
                .andExpect(status().isUnauthorized());
        assertThat(userRepository.findById(user.getId())).isPresent();
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/user/{id} -> 404 si absent")
    void delete_notFound() throws Exception {
        mockMvc.perform(delete("/api/user/9999"))
                .andExpect(status().isNotFound());
    }
}
