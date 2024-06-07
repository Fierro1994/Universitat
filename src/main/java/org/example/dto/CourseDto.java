package org.example.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CourseDto {
    private Long id;
    private String name;
    private Set<StudentDto> students = new HashSet<>();
    private TeacherDto teacher = new TeacherDto();

    public CourseDto() {
    }

    public CourseDto(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeacherDto getTeacherDto() {
        return teacher;
    }

    public void setTeacherDto(TeacherDto teacherDto) {
        this.teacher = teacherDto;
    }

    @Override
    public String toString() {
        if (teacher == null) {
            return "id: " + id +
                    ", name: " + name +
                    ", teacher: " + " не назначен";
        } else return "id: " + id +
                ", name: " + name +
                ", teacher: " + "id: " + teacher.getId() + " name: " + teacher.getName();
    }
}
