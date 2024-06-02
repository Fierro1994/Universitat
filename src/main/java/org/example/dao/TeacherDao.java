package org.example.dao;

import org.example.dao.interfaceDao.CrudDao;
import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.ExistEntityException;
import org.example.models.Course;
import org.example.models.Teacher;
import org.example.service.DBConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

public class TeacherDao implements CrudDao<Teacher> {
    private static final Logger logger = LoggerFactory.getLogger(TeacherDao.class);
    private final DBConnector dbConnector;

    public TeacherDao() {
        dbConnector = new DBConnector();
        init();
    }

    public TeacherDao(DBConnector dbConnector) {
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

    @Override
    public Optional<Teacher> getById(Long id) {
        String query = "SELECT name_teacher, email FROM teachers WHERE id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String teacherName = resultSet.getString("name_teacher");
                String email = resultSet.getString("email");
                Set<Course> courses = getCoursesForTeacher(id);
                Teacher teacher = new Teacher();
                teacher.setId(id);
                teacher.setEmail(email);
                teacher.setName(teacherName);
                teacher.setCourses(courses);
                return Optional.of(teacher);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Set<Teacher> getAll() {
        String query = "SELECT id, name_teacher, email FROM teachers";
        Set<Teacher> teachers = new HashSet<>();
        try (Connection connection = dbConnector.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                Teacher teacher = new Teacher();
                teacher.setId(resultSet.getLong("id"));
                teacher.setName(resultSet.getString("name_teacher"));
                teacher.setEmail(resultSet.getNString("email"));
                teacher.setCourses(getCoursesForTeacher(teacher.getId()));
                teachers.add(teacher);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return teachers;
    }

    @Override
    public void save(Teacher teacher) throws ExistEntityException, EntityNotFoundException {
        String query = "INSERT INTO teachers (name_teacher, email) VALUES (?, ?)";
        Optional<Teacher> existingTeacher = getByEmail(teacher.getEmail());
        if (existingTeacher.isPresent()) {
            throw new ExistEntityException("Teacher with email " + teacher.getEmail() + " already exists");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, teacher.getName());
                statement.setString(2, teacher.getEmail());
                statement.executeUpdate();
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long teacherId = generatedKeys.getLong(1);
                    teacher.setId(teacherId);
                    if (teacher.getCourses() != null) {
                        for (Course course : teacher.getCourses()) {
                            Optional<Course> existingCourse = getCourseByName(course.getName());
                            if (existingCourse.isPresent()) {
                                updateTeacherCourse(teacher, existingCourse.get());
                            } else {
                                throw new EntityNotFoundException("Course with name " + course.getName() + " not found");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error(e.getMessage());
            } catch (EntityNotFoundException e) {
                throw new EntityNotFoundException("Course not found");
            }
        }
    }

    @Override
    public void update(Teacher teacher) throws EntityNotFoundException {
        String query = "UPDATE teachers SET name_teacher = ? WHERE id = ?";
        CourseDao courseDao = new CourseDao(dbConnector);
        Optional<Teacher> existingTeacher = getByEmail(teacher.getEmail());
        if (existingTeacher.isEmpty()) {
            throw new EntityNotFoundException("Teacher with email " + teacher.getEmail() + " not found");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, teacher.getName());
                statement.setLong(2, teacher.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
            if (teacher.getCourses() != null) {
                for (Course course : teacher.getCourses()) {
                    Optional<Course> existingCourse = courseDao.getByName(course.getName());
                    if (existingCourse.isEmpty()) {
                        throw new EntityNotFoundException("Course with name " + course.getName() + " not found");
                    } else {
                        updateTeacherCourse(teacher, existingCourse.get());
                    }
                }

            }
        }
        teacher = getByEmail(teacher.getEmail()).get();
    }

    @Override
    public void remove(Teacher teacher) throws EntityNotFoundException {
        String query = "DELETE FROM teachers WHERE id = ?";
        Optional<Teacher> existingTeacher = getByEmail(teacher.getEmail());
        if (existingTeacher.isEmpty()) {
            throw new EntityNotFoundException("Teacher not found");
        } else {
            try (Connection connection = dbConnector.getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setLong(1, teacher.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private Optional<Teacher> getByEmail(String email) {
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

    private Set<Course> getCoursesForTeacher(Long teacherId) throws SQLException {
        String query = "SELECT id, name_course FROM courses WHERE teacher_id = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            Set<Course> courses = new HashSet<>();
            statement.setLong(1, teacherId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long courseId = resultSet.getLong("id");
                String courseName = resultSet.getString("name_course");
                Course course = new Course();
                course.setName(courseName);
                course.setId(courseId);
                courses.add(course);
            }
            return courses;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return Collections.emptySet();
    }


    private Optional<Course> getCourseByName(String name) throws SQLException {
        String query = "SELECT id, name_course FROM courses WHERE name_course = ?";
        try (Connection connection = dbConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            Course course = new Course();
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                course.setId(resultSet.getLong("id"));
                course.setName(resultSet.getString("name_course"));
                return Optional.of(course);
            }
            return Optional.empty();
        } catch (Exception e) {
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
}
