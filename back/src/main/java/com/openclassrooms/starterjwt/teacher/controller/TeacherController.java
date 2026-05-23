package com.openclassrooms.starterjwt.teacher.controller;

import com.openclassrooms.starterjwt.teacher.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.teacher.model.Teacher;
import com.openclassrooms.starterjwt.teacher.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {
    private final TeacherMapper teacherMapper;
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService, TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
        this.teacherService = teacherService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        Teacher teacher = this.teacherService.findById(id);
        return ResponseEntity.ok(this.teacherMapper.toDto(teacher));
    }

    @GetMapping
    public ResponseEntity<?> findAll() {
        List<Teacher> teachers = this.teacherService.findAll();
        return ResponseEntity.ok(this.teacherMapper.toDto(teachers));
    }
}
