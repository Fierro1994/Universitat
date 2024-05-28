package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.models.Course;
import java.util.HashSet;
import java.util.Set;

public class TeacherDto {
    private Long id;
    private String name;
    @JsonProperty("courses")
    private Set<Course> courses;

    public TeacherDto() {
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

    public Set<Course> getCourses() {
        return courses;
    }

    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    @Override
    public String toString() {
        Set<String> coursesNameList = new HashSet<>();
        if (courses != null){
            courses.forEach(e-> coursesNameList.add((e.getName())));
        }
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", courses=" + coursesNameList +
                '}';
    }
}
