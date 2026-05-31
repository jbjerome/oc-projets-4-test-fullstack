package com.openclassrooms.starterjwt.session.mapper;

import com.openclassrooms.starterjwt.session.dto.SessionDto;
import com.openclassrooms.starterjwt.session.model.Session;
import com.openclassrooms.starterjwt.teacher.model.Teacher;
import com.openclassrooms.starterjwt.teacher.repository.TeacherRepository;
import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SessionMapper - tests d'intégration")
class SessionMapperIT {

    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserRepository userRepository;

    private Teacher teacher;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        teacherRepository.deleteAll();
        teacher = teacherRepository.save(Teacher.builder().firstName("Margot").lastName("Delahaye").build());
        user = userRepository.save(new User("member@test.com", "Doe", "John", "pwd", false));
    }

    @Test
    @DisplayName("toEntity résout le teacher et les users à partir de leurs ids")
    void toEntity_withTeacherAndUsers() {
        SessionDto dto = new SessionDto();
        dto.setName("Yoga");
        dto.setDate(new Date());
        dto.setDescription("desc");
        dto.setTeacher_id(teacher.getId());
        dto.setUsers(List.of(user.getId()));

        Session entity = sessionMapper.toEntity(dto);

        assertThat(entity.getTeacher().getId()).isEqualTo(teacher.getId());
        assertThat(entity.getUsers()).extracting(User::getId).containsExactly(user.getId());
    }

    @Test
    @DisplayName("toEntity gère teacher_id null et users null")
    void toEntity_withNulls() {
        SessionDto dto = new SessionDto();
        dto.setName("Yoga");
        dto.setDate(new Date());
        dto.setDescription("desc");
        dto.setTeacher_id(null);
        dto.setUsers(null);

        Session entity = sessionMapper.toEntity(dto);

        assertThat(entity.getTeacher()).isNull();
        assertThat(entity.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("toDto expose teacher_id et la liste des ids d'utilisateurs")
    void toDto_withTeacherAndUsers() {
        Session session = Session.builder()
                .id(1L).name("Yoga").date(new Date()).description("desc")
                .teacher(teacher).users(new ArrayList<>(List.of(user))).build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto.getTeacher_id()).isEqualTo(teacher.getId());
        assertThat(dto.getUsers()).containsExactly(user.getId());
    }

    @Test
    @DisplayName("toDto gère teacher null et users null")
    void toDto_withNulls() {
        Session session = Session.builder()
                .id(2L).name("Yoga").date(new Date()).description("desc")
                .teacher(null).users(null).build();

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto.getTeacher_id()).isNull();
        assertThat(dto.getUsers()).isEmpty();
    }

    @Test
    @DisplayName("mappe les listes et les entrées null")
    void lists_and_nulls() {
        assertThat(sessionMapper.toDto((Session) null)).isNull();
        assertThat(sessionMapper.toEntity((SessionDto) null)).isNull();
        assertThat(sessionMapper.toDto(Collections.<Session>emptyList())).isEmpty();
        assertThat(sessionMapper.toEntity(Collections.<SessionDto>emptyList())).isEmpty();
    }
}
