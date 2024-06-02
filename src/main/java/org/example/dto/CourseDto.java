package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.models.Student;
import org.example.models.Teacher;

import java.util.HashSet;
import java.util.Set;

public class CourseDto {
    private Long id;
    private String name;

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

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
