package org.example.service;

import com.sun.jdi.connect.Connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class CreateDBAndDTO {
    private static final String SQL_CREATE_DB = "CREATE DATABASE dbmelody CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;;";
    private static final String SQL_DROP_DB = "DROP DATABASE dbmelody;";
    private Connection connection;
    public CreateDBAndDTO(Connection connection){
        this.connection = connection;
    }


    public void createDataBase() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(SQL_DROP_DB);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage().toString());
        }

        String initSQL = "USE dbmelody;";
        String SQLStusent =
                "CREATE TABLE students (\n" +
                        "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    email VARCHAR(255) UNIQUE,\n" +
                        "    name_student VARCHAR(255),\n" +
                        "    age INT\n" +
                        ");";
        String SQLTeacher =
                "CREATE TABLE teachers (\n" +
                        "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    email VARCHAR(255) UNIQUE,\n" +
                        "    name_teacher VARCHAR(255)\n" +
                        ");";
        String SQLCourses =
                "CREATE TABLE courses (\n" +
                        "    id INT AUTO_INCREMENT PRIMARY KEY,\n" +
                        "    name_course VARCHAR(255) UNIQUE,\n" +
                        "    teacher_id INT,\n" +
                        "    FOREIGN KEY (teacher_id) REFERENCES teachers(id)\n" +
                        ");";

        String SQLStudentsCourses =
                "CREATE TABLE students_and_courses (\n" +
                        "    student_id INT,\n" +
                        "    course_id INT,\n" +
                        "    PRIMARY KEY (student_id, course_id),\n" +
                        "    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,\n" +
                        "    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE\n" +
                        ");";

        String SQLOneToManyTeacherCourses = "ALTER TABLE dbmelody.courses ADD FOREIGN KEY (teacher_id) REFERENCES dbmelody.teachers(id) ON DELETE SET NULL";


        try  {
            statement = connection.createStatement();
            statement.execute(SQL_CREATE_DB);
            statement.execute(initSQL);
            statement.execute(SQLStusent);
            statement.execute(SQLTeacher);
            statement.execute(SQLCourses);
            statement.execute(SQLStudentsCourses);
            statement.execute(SQLOneToManyTeacherCourses);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage().toString());
        }
    }
}
