package com.openclassrooms.starterjwt.teacher.mapper;

import com.openclassrooms.starterjwt.shared.mapper.EntityMapper;
import com.openclassrooms.starterjwt.teacher.dto.TeacherDto;
import com.openclassrooms.starterjwt.teacher.model.Teacher;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TeacherMapper extends EntityMapper<TeacherDto, Teacher> {
}
