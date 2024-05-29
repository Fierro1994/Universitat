package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.models.Course;

import java.util.HashSet;
import java.util.Set;

public class StudentDto    {
    private Long id;
    private String email;
    private String name;
    private int age;
    @JsonProperty("сourses")
    @JsonIgnore
    private Set<Course> courses;

    public StudentDto() {
    }

    public StudentDto(String email, String name, int age) {
        this.email = email;
        this.name = name;
        this.age = age;
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
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", courses=" + coursesNameList + "}";
    }
}
