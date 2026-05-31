package com.openclassrooms.starterjwt.session.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.session.dto.SessionDto;
import com.openclassrooms.starterjwt.session.model.Session;
import com.openclassrooms.starterjwt.session.repository.SessionRepository;
import com.openclassrooms.starterjwt.teacher.model.Teacher;
import com.openclassrooms.starterjwt.teacher.repository.TeacherRepository;
import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@DisplayName("SessionController - tests d'intégration")
class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;

    private Teacher teacher;
    private User user;

    @BeforeEach
    void setUp() {
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();
        teacher = teacherRepository.save(Teacher.builder().firstName("Margot").lastName("Delahaye").build());
        user = userRepository.save(new User("member@test.com", "Doe", "John", "pwd", false));
    }

    private Session persistSession() {
        return sessionRepository.save(Session.builder()
                .name("Yoga").date(new Date()).description("Morning session")
                .teacher(teacher).users(new ArrayList<>()).build());
    }

    private SessionDto validDto() {
        SessionDto dto = new SessionDto();
        dto.setName("Yoga");
        dto.setDate(new Date());
        dto.setTeacher_id(teacher.getId());
        dto.setDescription("Morning session");
        return dto;
    }

    @Test
    @DisplayName("GET /api/session renvoie la liste")
    void findAll_returnsOk() throws Exception {
        persistSession();
        mockMvc.perform(get("/api/session"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/session/{id} renvoie la session")
    void findById_found() throws Exception {
        Session session = persistSession();
        mockMvc.perform(get("/api/session/" + session.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga"));
    }

    @Test
    @DisplayName("GET /api/session/{id} -> 404 si absente")
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/api/session/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/session crée la session")
    void create_success() throws Exception {
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yoga"));
        assertThat(sessionRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/session -> 400 si champ obligatoire manquant")
    void create_validationError() throws Exception {
        SessionDto dto = validDto();
        dto.setName(""); // @NotBlank violé
        mockMvc.perform(post("/api/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/session/{id} met à jour la session")
    void update_success() throws Exception {
        Session session = persistSession();
        SessionDto dto = validDto();
        dto.setName("Pilates");
        mockMvc.perform(put("/api/session/" + session.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pilates"));
    }

    @Test
    @DisplayName("DELETE /api/session/{id} supprime la session")
    void delete_success() throws Exception {
        Session session = persistSession();
        mockMvc.perform(delete("/api/session/" + session.getId()))
                .andExpect(status().isOk());
        assertThat(sessionRepository.findById(session.getId())).isEmpty();
    }

    @Test
    @DisplayName("POST participate deux fois -> 400 (déjà inscrit)")
    void participateTwice_returnsBadRequest() throws Exception {
        Session session = persistSession();

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST participate -> 404 si session absente")
    void participate_sessionNotFound() throws Exception {
        mockMvc.perform(post("/api/session/9999/participate/" + user.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST participate puis DELETE participate")
    void participateAndNoLongerParticipate() throws Exception {
        Session session = persistSession();

        mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
        assertThat(sessionRepository.findById(session.getId()).orElseThrow().getUsers())
                .extracting(User::getId).contains(user.getId());

        mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + user.getId()))
                .andExpect(status().isOk());
        assertThat(sessionRepository.findById(session.getId()).orElseThrow().getUsers())
                .extracting(User::getId).doesNotContain(user.getId());
    }
}
