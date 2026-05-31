package com.openclassrooms.starterjwt.teacher.mapper;

import com.openclassrooms.starterjwt.teacher.dto.TeacherDto;
import com.openclassrooms.starterjwt.teacher.model.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TeacherMapper - tests unitaires")
class TeacherMapperTest {

    private final TeacherMapper mapper = new TeacherMapperImpl();

    private Teacher teacher() {
        return Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
    }

    private TeacherDto dto() {
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setFirstName("Margot");
        dto.setLastName("Delahaye");
        return dto;
    }

    @Test
    @DisplayName("toDto mappe l'entité")
    void toDto_mapsEntity() {
        TeacherDto result = mapper.toDto(teacher());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLastName()).isEqualTo("Delahaye");
    }

    @Test
    @DisplayName("toEntity mappe le DTO")
    void toEntity_mapsDto() {
        Teacher result = mapper.toEntity(dto());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Margot");
    }

    @Test
    @DisplayName("mappe les listes dans les deux sens")
    void mapsLists() {
        assertThat(mapper.toDto(List.of(teacher()))).hasSize(1);
        assertThat(mapper.toEntity(List.of(dto()))).hasSize(1);
    }

    @Test
    @DisplayName("renvoie null pour des entrées null")
    void nullInputs_returnNull() {
        assertThat(mapper.toDto((Teacher) null)).isNull();
        assertThat(mapper.toEntity((TeacherDto) null)).isNull();
        assertThat(mapper.toDto((List<Teacher>) null)).isNull();
        assertThat(mapper.toEntity((List<TeacherDto>) null)).isNull();
    }
}
