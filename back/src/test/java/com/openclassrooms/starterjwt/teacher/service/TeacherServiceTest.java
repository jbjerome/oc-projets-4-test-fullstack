package com.openclassrooms.starterjwt.teacher.service;

import com.openclassrooms.starterjwt.teacher.exception.TeacherNotFoundException;
import com.openclassrooms.starterjwt.teacher.model.Teacher;
import com.openclassrooms.starterjwt.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TeacherService - tests unitaires")
class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    @DisplayName("findAll retourne la liste des enseignants")
    void findAll_returnsAllTeachers() {
        Teacher t1 = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        Teacher t2 = Teacher.builder().id(2L).firstName("Helene").lastName("Thiercelin").build();
        when(teacherRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Teacher> result = teacherService.findAll();

        assertThat(result).containsExactly(t1, t2);
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("findById retourne l'enseignant existant")
    void findById_whenExists_returnsTeacher() {
        Teacher teacher = Teacher.builder().id(1L).firstName("Margot").lastName("Delahaye").build();
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.findById(1L);

        assertThat(result).isEqualTo(teacher);
    }

    @Test
    @DisplayName("findById lève TeacherNotFoundException si absent")
    void findById_whenMissing_throws() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teacherService.findById(99L))
                .isInstanceOf(TeacherNotFoundException.class)
                .hasMessageContaining("99");
    }
}
