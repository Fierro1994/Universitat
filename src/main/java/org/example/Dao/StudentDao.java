package org.example.Dao;

import org.example.Dao.interfaceDao.CrudDao;
import org.example.models.Course;
import org.example.models.Student;

import java.sql.*;
import java.util.*;

public class StudentDao implements CrudDao<Student> {

    private final String DB_NAME = "dbmelody";
    private Connection connection;

    public StudentDao(Connection connection) {
        this.connection = connection;
        init();
    }

    private void init() {
        try {
            String initSQL = "USE " + DB_NAME + ";";
            PreparedStatement statement = connection.prepareStatement(initSQL);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Student> getById(Long id) {

        try {
            String query = "SELECT * FROM students WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public Set<Student> getAll() {
        Set<Student> students = new HashSet<>();
        try {
            String query = "SELECT * FROM students ORDER BY id";
            Statement statement = connection.createStatement();
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
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public void save(Student student) {
        CourseDao courseDao = new CourseDao(connection);
        Optional<Student> existingStudent = getByEmail(student.getEmail());
            if (existingStudent.isPresent()) {
                throw new RuntimeException("Student with email " + student.getEmail() + " already exists");
            } else {
                try {
                    String query = "INSERT INTO students (name_student, email, age) VALUES (?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, student.getName());
                    statement.setString(2, student.getEmail());
                    statement.setInt(3, student.getAge());
                    statement.executeUpdate();
                    ResultSet resultSet = statement.getGeneratedKeys();
                    if (resultSet.next()) {
                        student.setId(resultSet.getLong(1));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            if (student.getCourses() != null){
                Set<Course> checKCourse = new HashSet<>();
                for (Course course: student.getCourses()) {
                    Optional<Course> existingCourse = courseDao.getByName(course.getName());
                    if (!existingCourse.isPresent()) {
                        throw new RuntimeException("Course with name " + course.getName() + " not found");
                    }else {
                        checKCourse.add(existingCourse.get());

                    }

                }
                checkAndUpdateStudentCourse(student, checKCourse);
            }
        student = getByEmail(student.getEmail()).get();
        }


    @Override
    public void update(Student student) {
        CourseDao courseDao = new CourseDao(connection);
        Optional<Student> existingStudent = getByEmail(student.getEmail());
        if (!existingStudent.isPresent()) {
            throw new RuntimeException("Student with email " + student.getEmail() + " not found");
        } else {
            try {
                String query = "UPDATE students SET name_student = ?, email = ?, age = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, student.getName());
                statement.setString(2, student.getEmail());
                statement.setInt(3, student.getAge());
                statement.setLong(4, student.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (student.getCourses() != null){
                Set<Course> checKCourse = new HashSet<>();
                for (Course course: student.getCourses()) {
                    Optional<Course> existingCourse = courseDao.getByName(course.getName());
                    if (!existingCourse.isPresent()) {
                        throw new RuntimeException("Course with name " + course.getName() + " not found");
                    }else {
                        checKCourse.add(existingCourse.get());

                    }
                }
                checkAndUpdateStudentCourse(student, checKCourse);
            }
            student = getByEmail(student.getEmail()).get();
        }
    }

    @Override
    public void remove(Student student) {
        Optional<Student> existingStudent = getByEmail(student.getEmail());
        if (!existingStudent.isPresent()) {
            throw new RuntimeException("Student not found");
        }else {
            try {
                String query = "DELETE FROM students WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setLong(1, student.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<Student> getByEmail(String email) {
        try {
            String query = "SELECT * FROM students WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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
            e.printStackTrace();
        }

        return Optional.empty();
    }

private Set<Course> getCoursesForStudent(long studentId) throws SQLException {
    Set<Course> courses = new HashSet<>();
    String query = "SELECT * FROM students_and_courses WHERE student_id = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setLong(1, studentId);
    ResultSet resultSet = statement.executeQuery();
    while (resultSet.next()) {
        long courseId = resultSet.getLong("course_id");
        Optional<Course> course = getCourseById(courseId);
        if (course.isPresent()) {
            courses.add(course.get());
        }
    }
    return courses;
}

private Optional<Course> getCourseById(long courseId) throws SQLException {
    String query = "SELECT * FROM courses WHERE id = ?";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setLong(1, courseId);
    ResultSet resultSet = statement.executeQuery();
    if (resultSet.next()) {
        Course course = new Course();
        course.setId(resultSet.getLong("id"));
        course.setName(resultSet.getString("name_course"));
        return Optional.of(course);
    }
    return Optional.empty();
}


    private void checkAndUpdateStudentCourse(Student student, Set<Course> courses) {
        try {
            String query = "SELECT COUNT(*) FROM students_and_courses WHERE student_id =?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, student.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && resultSet.getInt(1) == 0) {
                for (Course course : courses) {
                    addStudentCourse(student, course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void addStudentCourse(Student student, Course course) {
        try {
            String query = "INSERT INTO students_and_courses (student_id, course_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, student.getId());
            statement.setLong(2, course.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
