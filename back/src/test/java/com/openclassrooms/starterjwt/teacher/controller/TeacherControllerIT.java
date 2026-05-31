package com.openclassrooms.starterjwt.teacher.controller;

import com.openclassrooms.starterjwt.teacher.model.Teacher;
import com.openclassrooms.starterjwt.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser
@DisplayName("TeacherController - tests d'intégration")
class TeacherControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    void clean() {
        teacherRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /api/teacher renvoie la liste")
    void findAll_returnsOk() throws Exception {
        teacherRepository.save(Teacher.builder().firstName("Margot").lastName("Delahaye").build());
        mockMvc.perform(get("/api/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/teacher/{id} renvoie l'enseignant")
    void findById_found() throws Exception {
        Teacher teacher = teacherRepository.save(
                Teacher.builder().firstName("Margot").lastName("Delahaye").build());
        mockMvc.perform(get("/api/teacher/" + teacher.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Delahaye"));
    }

    @Test
    @DisplayName("GET /api/teacher/{id} -> 404 si absent")
    void findById_notFound() throws Exception {
        mockMvc.perform(get("/api/teacher/9999"))
                .andExpect(status().isNotFound());
    }
}
