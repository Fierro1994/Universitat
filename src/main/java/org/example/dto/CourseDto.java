package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.models.Student;
import org.example.models.Teacher;

import java.util.HashSet;
import java.util.Set;

public class CourseDto {
    private Long id;
    private String name;
    private Set<Student> students;
    @JsonProperty("teacher")
    private Teacher teacher;

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

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    @Override
    public String toString() {
        Set<String> studentsNameList = new HashSet<>();
        String teacherName = "";
        if (students !=null){
            students.forEach(e-> studentsNameList.add((e.getName())));
        }
        if (teacher != null){
            teacherName = teacher.getName();
        }
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", students=" + studentsNameList +
                ", teacher=" + teacherName +
                '}';
    }
}
