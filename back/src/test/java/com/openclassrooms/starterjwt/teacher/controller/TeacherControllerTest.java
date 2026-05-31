package com.openclassrooms.starterjwt.teacher.controller;

import com.openclassrooms.starterjwt.teacher.dto.TeacherDto;
import com.openclassrooms.starterjwt.teacher.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.teacher.model.Teacher;
import com.openclassrooms.starterjwt.teacher.service.TeacherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherController - tests unitaires")
class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;
    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    @Test
    @DisplayName("findById renvoie l'enseignant mappé en DTO")
    void findById_returnsDto() {
        Teacher teacher = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        when(teacherService.findById(1L)).thenReturn(teacher);
        when(teacherMapper.toDto(teacher)).thenReturn(dto);

        ResponseEntity<?> response = teacherController.findById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    @DisplayName("findAll renvoie la liste des enseignants mappés")
    void findAll_returnsDtoList() {
        List<Teacher> teachers = List.of(Teacher.builder().id(1L).firstName("A").lastName("B").build());
        List<TeacherDto> dtos = List.of(new TeacherDto());
        when(teacherService.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teachers)).thenReturn(dtos);

        ResponseEntity<?> response = teacherController.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtos);
    }
}
