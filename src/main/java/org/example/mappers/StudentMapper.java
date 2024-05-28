package org.example.mappers;

import org.example.dto.StudentDto;
import org.example.models.Student;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentMapper {
 StudentMapper mapStudent = Mappers.getMapper(StudentMapper.class);
 public StudentDto toDto(Student student);
 public Student fromDto(StudentDto studentDto);
}
