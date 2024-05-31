package org.example.Dao;

import org.example.Dao.interfaceDao.CrudDao;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Exceptions.ExistEntityException;
import org.example.models.Course;
import org.example.models.Student;
import java.sql.*;
import java.util.*;
/**
 * Класс для работы со студентами в базе данных.
 */
public class StudentDao implements CrudDao<Student> {

    private final String DB_NAME = "dbmelody";
    private Connection connection;
    public StudentDao(Connection connection) {
        this.connection = connection;
        init();
    }
    /**
     * Инициализирует подключение к базе данных.
     */
    private void init() {
        try {
            String initSQL = "USE " + DB_NAME + ";";
            PreparedStatement statement = connection.prepareStatement(initSQL);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Получает студента из базы данных по указанному идентификатору.
     *
     * @param id идентификатор студента
     * @return опциональный объект студента с указанным идентификатором, если он найден, или пустой опциональ, если студент не найден
     */
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
    /**
     * Получает все студентов из базы данных.
     *
     * @return множество объектов студентов
     */
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
    /**
     * Сохраняет студента в базе данных.
     *
     * @param student объект студента для сохранения
     */
    @Override
    public void save(Student student) throws ExistEntityException, EntityNotFoundException {
        CourseDao courseDao = new CourseDao(connection);
        Optional<Student> existingStudent = getByEmail(student.getEmail());
        if (existingStudent.isPresent()) {
            throw new ExistEntityException("Student with email " + student.getEmail() + " already exists");
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
        if (student.getCourses() != null) {
            Set<Course> checKCourse = new HashSet<>();
            for (Course course : student.getCourses()) {
                Optional<Course> existingCourse = courseDao.getByName(course.getName());
                if (!existingCourse.isPresent()) {
                    throw new EntityNotFoundException("Course with name " + course.getName() + " not found");
                } else {
                    checKCourse.add(existingCourse.get());

                }

            }
            checkAndUpdateStudentCourse(student, checKCourse);
        }
        student = getByEmail(student.getEmail()).get();
    }

    /**
     * Обновляет данные студента в базе данных.
     *
     * @param student объект студента с обновленными данными
     */
    @Override
    public void update(Student student) throws EntityNotFoundException {
        CourseDao courseDao = new CourseDao(connection);
        Optional<Student> existingStudent = getByEmail(student.getEmail());
        if (!existingStudent.isPresent()) {
            throw new EntityNotFoundException("Student with email " + student.getEmail() + " not found");
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

            if (student.getCourses() != null) {
                Set<Course> checKCourse = new HashSet<>();
                for (Course course : student.getCourses()) {
                    Optional<Course> existingCourse = courseDao.getByName(course.getName());
                    if (!existingCourse.isPresent()) {
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
    /**
     * Удаляет студента из базы данных.
     *
     * @param student объект студента для удаления
     */
    @Override
    public void remove(Student student) throws EntityNotFoundException {
        Optional<Student> existingStudent = getByEmail(student.getEmail());
        if (!existingStudent.isPresent()) {
            throw new EntityNotFoundException("Student not found");
        } else {
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
    /**
     * Получает студента из базы данных по-указанному email.
     *
     * @param email email студента
     * @return студент с указанным email, если он найден, или пустой объект, если студент не найден
     */
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
    /**
     * Получает набор курсов для студента из базы данных.
     *
     * @param studentId идентификатор студента
     * @return набор курсов, которые принадлежат студенту
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
     */
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
    /**
     * Получает курс из базы данных по указанному идентификатору.
     *
     * @param courseId идентификатор курса
     * @return опциональный объект курса с указанным идентификатором, если он найден, или пустой опциональ, если курс не найден
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
     */
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

    /**
     * Проверяет, есть ли у студента уже курсы в базе данных, и, если нет, добавляет указанные курсы.
     *
     * @param student студент, для которого добавляются курсы
     * @param courses набор курсов для добавления
     */
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
    /**
     * Добавляет связь студента и курс в базу данных.
     *
     * @param student студент
     * @param course курс
     */
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
