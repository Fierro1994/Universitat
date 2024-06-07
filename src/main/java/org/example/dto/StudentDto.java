package org.example.dto;

import org.example.models.Course;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StudentDto {
    private Long id;
    private String email;
    private String name;
    private int age;
    private Set<CourseDto> courses = new HashSet<>();

    public StudentDto() {
    }

    public StudentDto(String email, String name, int age) {
        this.email = email;
        this.name = name;
        this.age = age;
    }

    public Set<CourseDto> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseDto> courses) {
        this.courses = courses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        Map<Long, String> courserName = new HashMap<>();
        courses.forEach(courses ->{
            courserName.put(courses.getId(), courses.getName());

        });
        return  "id: " + id +
                ", email: " + email +
                ", name: " + name +
                ", age: " + age +
                ", courses: " + courserName;
    }
}
