package org.example.dao;

import org.example.dao.interfaceDao.CrudDao;
import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.ExistEntityException;
import org.example.models.Course;
import org.example.models.Student;
import org.example.models.Teacher;
import org.example.service.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


public class CourseDao implements CrudDao<Course> {
    private static final Logger logger = LoggerFactory.getLogger(CourseDao.class);
    private final DBConnector dbConnector;

    public CourseDao() {
        dbConnector = new DBConnector();
        init();
    }

    public CourseDao(DBConnector dbConnector) {
        this.dbConnector = dbConnector;
        init();
    }


    private void init() {
        String dbName = "dbmelody";
        String initSQL = "USE " + dbName + ";";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(initSQL)) {
            statement.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public Optional<Course> getByName(String name) {
        String query = "SELECT id, name_course FROM courses WHERE name_course = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long id = resultSet.getLong("id");
                String courseName = resultSet.getString("name_course");
                Set<Student> students = getStudentsForCourse(id);
                Teacher teacher = getTeacherForCourse(id);
                Course course = new Course();
                course.setId(id);
                course.setName(courseName);
                course.setStudents(students);
                course.setTeacher(teacher);

                return Optional.of(course);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Course> getById(Long id) {
        String query = "SELECT name_course FROM courses WHERE id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String courseName = resultSet.getString("name_course");
                Set<Student> students = getStudentsForCourse(id);
                Teacher teacher = getTeacherForCourse(id);
                Course course = new Course();
                course.setId(id);
                course.setName(courseName);
                course.setStudents(students);
                course.setTeacher(teacher);
                return Optional.of(course);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Set<Course> getAll() {
        Set<Course> courses = new HashSet<>();
        String query = "SELECT id, name_course, teacher_id FROM courses";
        try (Connection connection = dbConnector.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Course course = new Course();
                course.setId(resultSet.getLong("id"));
                course.setName(resultSet.getString("name_course"));
                if (resultSet.getLong("teacher_id") != 0) {
                    course.setTeacher(getTeacherById(resultSet.getLong("teacher_id")).get());
                }
                course.setStudents(getStudentsForCourse(course.getId()));
                courses.add(course);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return courses;
    }

    @Override
    public void save(Course course) throws ExistEntityException, EntityNotFoundException {
        String query = "INSERT INTO courses (name_course) VALUES (?)";
        Optional<Course> existsCourseByName = getByName(course.getName());
        if (existsCourseByName.isPresent()) {
            logger.error("Course with name {} already exists", course.getName());
            throw new ExistEntityException("Course with name " + course.getName() + " already exists");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ) {
                statement.setString(1, course.getName());
                statement.executeUpdate();
                ResultSet resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    long courseId = resultSet.getLong(1);
                    course.setId(courseId);
                    if (course.getStudents() != null) {
                        for (Student student : course.getStudents()) {
                            Optional<Student> existingStudent = getStudentByEmail(student.getEmail());
                            if (existingStudent.isPresent()) {
                                checkAndUpdateStudentCourse(existingStudent.get(), course);
                            } else {
                                throw new EntityNotFoundException("Student with email " + student.getEmail() + " not found");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            if (course.getTeacher() != null) {
                Optional<Teacher> existingTeacher = getTeacherByEmail(course.getTeacher().getEmail());
                if (existingTeacher.isPresent()) {
                    updateTeacherCourse(existingTeacher.get(), course);
                } else {
                    throw new EntityNotFoundException("Teacher with name " + course.getTeacher().getName() + " not found");
                }
            }

        }
    }

    @Override
    public void update(Course course) throws EntityNotFoundException {
        String query = "UPDATE courses SET name_course = ? WHERE id = ?";
        Optional<Course> existingCourse = getByName(course.getName());
        if (existingCourse.isEmpty()) {
            throw new EntityNotFoundException("Course with name " + course.getName() + " not found");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, course.getName());
                statement.setLong(2, course.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            if (course.getTeacher() != null) {
                Optional<Teacher> existingTeacher = getTeacherByEmail(course.getTeacher().getEmail());
                if (existingTeacher.isPresent()) {
                    updateTeacherCourse(existingTeacher.get(), course);
                } else {
                    throw new EntityNotFoundException("Teacher with name " + course.getTeacher().getName() + " not found");
                }
            }
            if (course.getStudents() != null) {
                for (Student student : course.getStudents()) {
                    Optional<Student> existingStudent = getStudentByEmail(student.getEmail());
                    if (existingStudent.isPresent()) {
                        checkAndUpdateStudentCourse(existingStudent.get(), course);
                    } else {
                        throw new EntityNotFoundException("Student with email " + student.getEmail() + " not found");
                    }
                }
            }

            course = getByName(course.getName()).get();
        }
    }

    @Override
    public void remove(Course course) throws EntityNotFoundException {
        String query = "DELETE FROM courses WHERE id = ?";
        Optional<Course> existingCourse = getByName(course.getName());
        if (existingCourse.isEmpty()) {
            throw new EntityNotFoundException("Course not found");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, course.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private Optional<Student> getStudentByEmail(String email) {
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
                return Optional.of(student);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    private void checkAndUpdateStudentCourse(Student student, Course course) {
        String query = "SELECT COUNT(*) FROM students_and_courses WHERE student_id = ? and course_id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, student.getId());
            statement.setLong(2, course.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && (resultSet.getInt(1) > 0)) {
                updateStudentCourse(student, course);
            } else addStudentCourse(student, course);
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private void updateStudentCourse(Student student, Course course) {
        String query = "UPDATE students_and_courses SET student_id = ?, course_id = ? WHERE student_id = ? AND course_id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setLong(1, student.getId());
            statement.setLong(2, course.getId());
            statement.setLong(3, student.getId());
            statement.setLong(4, course.getId());
            statement.executeUpdate();
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

    private Set<Student> getStudentsForCourse(long courseId) throws SQLException {
        String query = "SELECT student_id FROM students_and_courses WHERE course_id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            Set<Student> students = new HashSet<>();
            statement.setLong(1, courseId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                long studentId = resultSet.getLong("student_id");
                Optional<Student> student = getStudentById(studentId);
                student.ifPresent(students::add);
            }
            return students;
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Collections.emptySet();
    }

    private Optional<Teacher> getTeacherByEmail(String email) {
        String query = "SELECT id, name_teacher, email FROM teachers WHERE email = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(resultSet.getLong("id"));
                teacher.setName(resultSet.getString("name_teacher"));
                teacher.setEmail(resultSet.getString("email"));
                return Optional.of(teacher);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }

        return Optional.empty();
    }

    private void updateTeacherCourse(Teacher teacher, Course course) {
        String query = "UPDATE courses SET teacher_id = ? WHERE id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, teacher.getId());
            statement.setLong(2, course.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    private Teacher getTeacherForCourse(long courseId) throws SQLException {
        String query = "SELECT teacher_id FROM courses WHERE id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, courseId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                long teacherId = resultSet.getLong("teacher_id");
                Optional<Teacher> teacher = getTeacherById(teacherId);
                if (teacher.isPresent()) {
                    return teacher.get();
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return null;

    }

    private Optional<Student> getStudentById(Long id) {
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
                return Optional.of(student);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }


    private Optional<Teacher> getTeacherById(Long id) {
        String query = "SELECT id, name_teacher, email FROM teachers WHERE id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(resultSet.getLong("id"));
                teacher.setName(resultSet.getString("name_teacher"));
                teacher.setEmail(resultSet.getString("email"));
                return Optional.of(teacher);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }
}