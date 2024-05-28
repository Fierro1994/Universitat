package org.example.mappers;

import org.example.dto.TeacherDto;
import org.example.models.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface TeacherMapper {
    TeacherMapper mapTeacher = Mappers.getMapper(TeacherMapper.class);
    public TeacherDto toDto(Teacher teacher);
    public Teacher fromDto(TeacherDto teacherDto);
}
