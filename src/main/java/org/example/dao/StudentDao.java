package org.example.dao;

import org.example.dao.interfaceDao.CrudDao;
import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.ExistEntityException;
import org.example.models.Course;
import org.example.models.Student;
import org.example.service.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class StudentDao implements CrudDao<Student> {
    private static final Logger logger = LoggerFactory.getLogger(StudentDao.class);

    private final DBConnector dbConnector;

    public StudentDao() {
        dbConnector = new DBConnector();
    }

    public StudentDao(DBConnector dbConnector) {
        this.dbConnector = dbConnector;

    }

    @Override
    public Optional<Student> getById(Long id) {
        String query = "SELECT id, name_student, email, age FROM students WHERE id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getLong("id"));
                student.setName(resultSet.getString("name_student"));
                student.setEmail(resultSet.getString("email"));
                student.setAge(resultSet.getInt("age"));
                student.setCourses(getCoursesForStudent(student.getId()));
                return Optional.of(student);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Set<Student> getAll() {
        String query = "SELECT id, name_student, email, age FROM students ORDER BY id";
        Set<Student> students = new HashSet<>();
        try (Connection connection = dbConnector.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getLong("id"));
                student.setName(resultSet.getString("name_student"));
                student.setEmail(resultSet.getString("email"));
                student.setAge(resultSet.getInt("age"));
                student.setCourses(getCoursesForStudent(student.getId()));
                students.add(student);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return students;
    }

    @Override
    public void save(Student student) throws ExistEntityException, EntityNotFoundException {
        String query = "INSERT INTO students (name_student, email, age) VALUES (?, ?, ?)";
        Optional<Student> existingStudent = getByEmail(student.getEmail());
        if (existingStudent.isPresent()) {
            throw new ExistEntityException("Student with email " + student.getEmail() + " already exists");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, student.getName());
                statement.setString(2, student.getEmail());
                statement.setInt(3, student.getAge());
                statement.executeUpdate();
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    student.setId(resultSet.getLong(1));
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
        if (student.getCourses() != null) {
            Set<Course> checKCourse = new HashSet<>();
            for (Course course : student.getCourses()) {
                CourseDao courseDao = new CourseDao(dbConnector);
                Optional<Course> existingCourse = courseDao.getByName(course.getName());
                if (existingCourse.isEmpty()) {
                    throw new EntityNotFoundException("Course with name " + course.getName() + " not found");
                } else {
                    checKCourse.add(existingCourse.get());
                }
            }
            checkAndUpdateStudentCourse(student, checKCourse);
        }
        student = getByEmail(student.getEmail()).get();
    }

    @Override
    public void update(Student student) throws EntityNotFoundException {
        String query = "UPDATE students SET name_student = ?, email = ?, age = ? WHERE id = ?";
        CourseDao courseDao = new CourseDao(dbConnector);
        Optional<Student> existingStudent = getByEmail(student.getEmail());
        if (existingStudent.isEmpty()) {
            throw new EntityNotFoundException("Student with email " + student.getEmail() + " not found");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, student.getName());
                statement.setString(2, student.getEmail());
                statement.setInt(3, student.getAge());
                statement.setLong(4, student.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }

            if (student.getCourses() != null) {
                Set<Course> checKCourse = new HashSet<>();
                for (Course course : student.getCourses()) {
                    Optional<Course> existingCourse = courseDao.getByName(course.getName());
                    if (existingCourse.isEmpty()) {
                        throw new EntityNotFoundException("Course with name " + course.getName() + " not found");
                    } else {
                        checKCourse.add(existingCourse.get());
                    }
                }
                checkAndUpdateStudentCourse(student, checKCourse);
            }
            student = getByEmail(student.getEmail()).get();
        }
    }

    @Override
    public void remove(Student student) throws EntityNotFoundException {
        String query = "DELETE FROM students WHERE id = ?";
        Optional<Student> existingStudent = getByEmail(student.getEmail());
        if (existingStudent.isEmpty()) {
            throw new EntityNotFoundException("Student not found");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, student.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public Optional<Student> getByEmail(String email) {
        String query = "SELECT id, name_student, email, age FROM students WHERE email = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getLong("id"));
                student.setName(resultSet.getString("name_student"));
                student.setEmail(resultSet.getString("email"));
                student.setAge(resultSet.getInt("age"));
                student.setCourses(getCoursesForStudent(student.getId()));
                return Optional.of(student);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    private Set<Course> getCoursesForStudent(long studentId) throws SQLException {
        String query = "SELECT course_id FROM students_and_courses WHERE student_id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            Set<Course> courses = new HashSet<>();
            statement.setLong(1, studentId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long courseId = resultSet.getLong("course_id");
                Optional<Course> course = getCourseById(courseId);
                course.ifPresent(courses::add);
            }
            return courses;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Collections.emptySet();
    }

    private Optional<Course> getCourseById(long courseId) throws SQLException {
        String query = "SELECT id, name_course FROM courses WHERE id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, courseId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Course course = new Course();
                course.setId(resultSet.getLong("id"));
                course.setName(resultSet.getString("name_course"));
                return Optional.of(course);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }


    private void checkAndUpdateStudentCourse(Student student, Set<Course> courses) {
        String query = "SELECT COUNT(*) FROM students_and_courses WHERE student_id =?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, student.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                for (Course course : courses) {
                    addStudentCourse(student, course);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

    }

    private void addStudentCourse(Student student, Course course) {
        String query = "INSERT INTO students_and_courses (student_id, course_id) VALUES (?, ?)";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, student.getId());
            statement.setLong(2, course.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }
}
