package org.example;

import org.example.Dao.CourseDao;
import org.example.Dao.StudentDao;
import org.example.Dao.TeacherDao;
import org.example.models.Course;
import org.example.models.Student;
import org.example.models.Teacher;
import org.example.service.CreateDBAndDTO;
import org.example.service.DBConnector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class Main {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        DBConnector dbConnector = new DBConnector();
        CreateDBAndDTO createDBAndDTO = new CreateDBAndDTO(dbConnector.getConnection());
        createDBAndDTO.createDataBase();
        StudentDao studentDao = new StudentDao(dbConnector.getConnection());

        CourseDao courseDao = new CourseDao(dbConnector.getConnection());
        TeacherDao teacherDao = new TeacherDao(dbConnector.getConnection());
        Student student = new Student("1@mail.ru", "1");
        Student student2 = new Student("2@mail.ru", "2", 1);
        Course course = new Course("java");
        Course course1 = new Course("python");
        Course course2 = new Course("js");
        courseDao.save(course);
        courseDao.save(course1);
        courseDao.save(course2);
        Set<Course> courses = new HashSet<>();
        courses.add(course);
        courses.add(course1);
        courses.add(course2);
        student.setCourses(courses);
        studentDao.save(student);
        studentDao.save(student2);

        Optional<Student> result = studentDao.getById(student.getId());
        Optional<Student> result2 = studentDao.getById(student2.getId());
        System.out.println(result);
        System.out.println(result2);

    }
}