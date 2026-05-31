package com.openclassrooms.starterjwt.session.model;

import com.openclassrooms.starterjwt.teacher.model.Teacher;
import com.openclassrooms.starterjwt.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Session (modèle) - tests unitaires")
class SessionModelTest {

    @Test
    @DisplayName("builder, constructeur complet et accesseurs chaînés")
    void builderAllArgsAndAccessors() {
        Date date = new Date();
        LocalDateTime now = LocalDateTime.now();
        Teacher teacher = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        List<User> users = new ArrayList<>();

        Session built = Session.builder()
                .id(1L).name("Yoga").date(date).description("desc")
                .teacher(teacher).users(users).createdAt(now).updatedAt(now).build();

        assertThat(built.getId()).isEqualTo(1L);
        assertThat(built.getName()).isEqualTo("Yoga");
        assertThat(built.getDate()).isEqualTo(date);
        assertThat(built.getDescription()).isEqualTo("desc");
        assertThat(built.getTeacher()).isEqualTo(teacher);
        assertThat(built.getUsers()).isSameAs(users);
        assertThat(built.getCreatedAt()).isEqualTo(now);
        assertThat(built.getUpdatedAt()).isEqualTo(now);
        assertThat(Session.builder().toString()).isNotNull();

        Session full = new Session(2L, "Pilates", date, "d", teacher, users, now, now);
        assertThat(full.getName()).isEqualTo("Pilates");

        Session viaSetters = new Session()
                .setId(3L).setName("Stretch").setDate(date).setDescription("new")
                .setTeacher(teacher).setUsers(users).setCreatedAt(now).setUpdatedAt(now);
        assertThat(viaSetters.getName()).isEqualTo("Stretch");
        assertThat(viaSetters.toString()).contains("Stretch");
    }

    @Test
    @DisplayName("equals/hashCode basés sur l'id, avec cas limites")
    void equalsAndHashCode() {
        Session a = Session.builder().id(1L).name("A").build();
        Session sameId = Session.builder().id(1L).name("B").build();
        Session otherId = Session.builder().id(2L).name("A").build();

        assertThat(a).isEqualTo(a);
        assertThat(a).isEqualTo(sameId).hasSameHashCodeAs(sameId);
        assertThat(a).isNotEqualTo(otherId);
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("a string");

        Session nullId1 = Session.builder().name("X").build();
        Session nullId2 = Session.builder().name("Y").build();
        assertThat(nullId1).isEqualTo(nullId2);
    }
}
