package com.openclassrooms.starterjwt.teacher.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Teacher (modèle) - tests unitaires")
class TeacherModelTest {

    @Test
    @DisplayName("builder, constructeurs et accesseurs")
    void builderAndAccessors() {
        LocalDateTime now = LocalDateTime.now();
        Teacher built = Teacher.builder()
                .id(1L).firstName("Margot").lastName("Delahaye")
                .createdAt(now).updatedAt(now).build();

        assertThat(built.getId()).isEqualTo(1L);
        assertThat(built.getFirstName()).isEqualTo("Margot");
        assertThat(built.getLastName()).isEqualTo("Delahaye");
        assertThat(built.getCreatedAt()).isEqualTo(now);
        assertThat(built.getUpdatedAt()).isEqualTo(now);
        assertThat(Teacher.builder().toString()).isNotNull();

        Teacher full = new Teacher(2L, "Thiercelin", "Helene", now, now);
        assertThat(full.getLastName()).isEqualTo("Thiercelin");

        Teacher viaSetters = new Teacher()
                .setId(3L).setFirstName("F").setLastName("L").setCreatedAt(now).setUpdatedAt(now);
        assertThat(viaSetters.getFirstName()).isEqualTo("F");
        assertThat(viaSetters.toString()).contains("F");
    }

    @Test
    @DisplayName("equals/hashCode basés sur l'id, avec cas limites")
    void equalsAndHashCode() {
        Teacher a = Teacher.builder().id(1L).firstName("A").lastName("A").build();
        Teacher sameId = Teacher.builder().id(1L).firstName("B").lastName("B").build();
        Teacher otherId = Teacher.builder().id(2L).firstName("A").lastName("A").build();

        assertThat(a).isEqualTo(a);
        assertThat(a).isEqualTo(sameId).hasSameHashCodeAs(sameId);
        assertThat(a).isNotEqualTo(otherId);
        assertThat(a).isNotEqualTo(null);
        assertThat(a).isNotEqualTo("a string");

        Teacher nullId1 = Teacher.builder().firstName("X").lastName("X").build();
        Teacher nullId2 = Teacher.builder().firstName("Y").lastName("Y").build();
        assertThat(nullId1).isEqualTo(nullId2);
    }
}
