package org.example.mappers;

import org.example.dto.StudentDto;
import org.example.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentMapper {
 StudentMapper mapStudent = Mappers.getMapper(StudentMapper.class);
 @Mapping(target = "courses", source = "courses")
 public StudentDto toDto(Student student);
 @Mapping(target = "courses", source = "courses")
 public Student fromDto(StudentDto studentDto);
}
