package org.example.service;

import org.example.dao.CourseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDBAndDTO {
    private static final Logger logger = LoggerFactory.getLogger(CourseDao.class);
    private static final String SQL_CREATE_DB = "CREATE DATABASE dbmelody CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;;";
    private static final String SQL_DROP_DB = "DROP DATABASE IF EXISTS dbmelody;";

    private final DBConnector dbConnector;

    public CreateDBAndDTO() {
        dbConnector = new DBConnector();
    }

    public CreateDBAndDTO(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
    }


    public void createDataBase() {


        String initSQL = "USE dbmelody;";
        String sqlStusent = """
                CREATE TABLE students (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(255) UNIQUE,
                    name_student VARCHAR(255),
                    age INT
                );
                """;
        String sqlTeacher = """
                CREATE TABLE teachers (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    email VARCHAR(255) UNIQUE,
                    name_teacher VARCHAR(255)
                );
                """;
        String sqlCourses = """
                CREATE TABLE courses (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name_course VARCHAR(255) UNIQUE,
                    teacher_id INT,
                    FOREIGN KEY (teacher_id) REFERENCES teachers(id)
                );
                """;

        String sqlStudentsCourses = """
                CREATE TABLE students_and_courses (
                    student_id INT,
                    course_id INT,
                    PRIMARY KEY (student_id, course_id),
                    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
                    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
                );
                """;
        String sqlOneToManyTeacherCourses = "ALTER TABLE dbmelody.courses ADD FOREIGN KEY (teacher_id) REFERENCES dbmelody.teachers(id) ON DELETE SET NULL";

        try (Connection connection = dbConnector.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(SQL_DROP_DB);
            statement.execute(SQL_CREATE_DB);
            statement.execute(initSQL);
            statement.execute(sqlStusent);
            statement.execute(sqlTeacher);
            statement.execute(sqlCourses);
            statement.execute(sqlStudentsCourses);
            statement.execute(sqlOneToManyTeacherCourses);

            logger.info("База данных успешно создана");
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
