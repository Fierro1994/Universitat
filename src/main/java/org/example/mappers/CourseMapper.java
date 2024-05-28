package org.example.mappers;

import org.example.dto.CourseDto;
import org.example.models.Course;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface CourseMapper {
    CourseMapper mapCourse = Mappers.getMapper(CourseMapper.class);
    CourseDto toDto(Course course);
    Course fromDto(CourseDto courseDto);
}
