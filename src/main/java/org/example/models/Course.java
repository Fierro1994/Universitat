package org.example.models;

import java.util.HashSet;
import java.util.Set;

public class Course {
    private Long id;
    private String name;
    private Set<Student> students = new HashSet<>();
    private Teacher teacher;

    public Course() {
    }

    public Course(String name) {
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
        if (teacher == null) {
            return "id: " + id +
                    ", name: " + name +
                    ", teacher: " + " не назначен";
        } else return "id: " + id +
                ", name: " + name +
                ", teacher: " + "id: " + teacher.getId() + " name: " + teacher.getName();
    }

}
