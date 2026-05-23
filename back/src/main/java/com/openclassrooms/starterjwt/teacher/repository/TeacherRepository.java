package com.openclassrooms.starterjwt.teacher.repository;

import com.openclassrooms.starterjwt.teacher.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
