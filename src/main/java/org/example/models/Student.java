package org.example.models;
import java.util.HashSet;
import java.util.Set;

public class Student {
    private Long id;
    private String email;
    private String name;
    private int age;

    private Set<Course> courses;

    public Student() {
    }

    public Student(String email, String name) {
        this.email = email;
        this.name = name;
    }
    public Student(String email, String name, int age) {
        this.email = email;
        this.name = name;
        this.age = age;
    }

    public Student(String email, String name, int age, Set<Course> courses) {
        this.email = email;
        this.name = name;
        this.age = age;
        this.courses = courses;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
                ", email='" + email + '\'' +
                ", courses=" + coursesNameList + "}";
    }
}
