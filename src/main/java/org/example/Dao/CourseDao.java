package org.example.Dao;

import org.example.Dao.interfaceDao.CrudDao;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Exceptions.ExistEntityException;
import org.example.models.Course;
import org.example.models.Student;
import org.example.models.Teacher;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
/**
 * Класс для работы с курсами в базе данных.
 */
public class CourseDao implements CrudDao<Course> {

    private final String DB_NAME = "dbmelody";
    private Connection connection;

    public CourseDao (Connection connection){
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
     * Получение курса по имени.
     * @param name - имя курса
     * @return Optional<Course> - курс, если он существует, Optional.empty() - если нет
     */
    public Optional<Course> getByName(String name) {
        try {
            String query = "SELECT * FROM courses WHERE name_course = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Получение курса по идентификатору.
     * @param id - идентификатор курса
     * @return Optional<Course> - курс, если он существует, Optional.empty() - если нет
     */
    @Override
    public Optional<Course> getById(Long id) {
        try {
            String query = "SELECT * FROM courses WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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
            e.printStackTrace();
        }
        return Optional.empty();
    }
    /**
     * Получение всех курсов.
     * @return Set<Course> - набор всех курсов
     */
    @Override
    public Set<Course> getAll() {
        Set<Course> courses = new HashSet<>();
        try {
            String query = "SELECT * FROM courses";
            Statement statement = connection.createStatement();
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
            e.printStackTrace();
        }
        return courses;
    }
    /**
     * Сохранение курса.
     * @param course - курс для сохранения
     */
    @Override
    public void save(Course course) throws ExistEntityException, EntityNotFoundException {
        Optional<Course> existsCourseByName = getByName(course.getName());
        if (existsCourseByName.isPresent()) {
            throw new ExistEntityException("Course with name " + course.getName() + " already exists");
        } else {
            try {
                String query = "INSERT INTO courses (name_course) VALUES (?)";
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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
                                throw new RuntimeException("Student with email " + student.getEmail() + " not found");
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
    /**
     * Обновление курса.
     * @param course - курс для обновления
     */
    @Override
    public void update(Course course) throws EntityNotFoundException {
        Optional<Course> existingCourse = getByName(course.getName());
        if (!existingCourse.isPresent()) {
            throw new EntityNotFoundException("Course with name " + course.getName() + " not found");
        } else {
            try {
                String query = "UPDATE courses SET name_course = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, course.getName());
                statement.setLong(2, course.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
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
    /**
     * Удаление курса.
     * @param course - курс для удаления
     */
    @Override
    public void remove(Course course) {
        Optional<Course> existingCourse = getByName(course.getName());
        if (!existingCourse.isPresent()) {
            throw new RuntimeException("Course not found");
        } else {
            try {
                String query = "DELETE FROM courses WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setLong(1, course.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Получает студента по электронной почте.
     *
     * @param email электронная почта студента
     * @return студент, если найден, или пустой объект, если студент не найден
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
     */
    private Optional<Student> getStudentByEmail(String email) {
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
                return Optional.of(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Проверяет и обновляет связь между студентом и курсом.
     *
     * @param student студент, связь которого нужно проверить и обновить
     * @param course курс, связь которого нужно проверить и обновить
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
     */
    private void checkAndUpdateStudentCourse(Student student, Course course) {
        try {
            String query = "SELECT COUNT(*) FROM students_and_courses WHERE student_id = ? and course_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, student.getId());
            statement.setLong(2, course.getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && (resultSet.getInt(1) > 0)) {
                updateStudentCourse(student, course);
            } else addStudentCourse(student, course);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Обновляет связь между студентом и курсом в таблице students_and_courses.
     *
     * @param student студент, связь которого нужно обновить
     * @param course курс, связь которого нужно обновить
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
     */
    private void updateStudentCourse(Student student, Course course) {
        try {
            String query = "UPDATE students_and_courses SET student_id = ?, course_id = ? WHERE student_id = ? AND course_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, student.getId());
            statement.setLong(2, course.getId());
            statement.setLong(3, student.getId());
            statement.setLong(4, course.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Добавляет связь между студентом и курсом в таблицу students_and_courses.
     *
     * @param student студент, связь которого нужно добавить
     * @param course курс, связь которого нужно добавить
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
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
    /**
     * Получает Set студентов, связанных с указанным курсом.
     *
     * @param courseId идентификатор курса
     * @return Set студентов, связанных с курсом
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
     */
    private Set<Student> getStudentsForCourse(long courseId) throws SQLException {
        Set<Student> students = new HashSet<>();
        String query = "SELECT * FROM students_and_courses WHERE course_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, courseId);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            long studentId = resultSet.getLong("student_id");
            Optional<Student> student = getStudentById(studentId);
            if (student.isPresent()) {
                students.add(student.get());
            }
        }
        return students;
    }
/**
 * Получает учителя по электронной почте.
 *
 * @param email электронная почта учителя
 * @return учитель с указанной электронной почтой, если он существует, иначе пустой Optional
 * @throws SQLException если возникает ошибка при выполнении запроса
 */
    private Optional<Teacher> getTeacherByEmail(String email) {
        try {
            String query = "SELECT * FROM teachers WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Обновляет учителя для указанного курса.
     *
     * @param teacher учитель, который будет связан с курсом
     * @param course курс, который будет обновлен с новым учителем
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
     */
    private void updateTeacherCourse(Teacher teacher, Course course) {
        try {
            String query = "UPDATE courses SET teacher_id = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, teacher.getId());
            statement.setLong(2, course.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Получает учителя, связанного с указанным курсом.
     *
     * @param courseId идентификатор курса
     * @return учитель, связанный с курсом, или null, если учитель не найден
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private Teacher getTeacherForCourse(long courseId) throws SQLException {
        String query = "SELECT * FROM courses WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, courseId);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            long teacherId = resultSet.getLong("teacher_id");
            Optional<Teacher> teacher = getTeacherById(teacherId);
            if (teacher.isPresent()) {
                return teacher.get();
            }
        }
        return null;
    }
    /**
     * Получает студента по его идентификатору.
     *
     * @param id идентификатор студента
     * @return студент с указанным идентификатором, если он существует, иначе пустой Optional
     * @throws SQLException если возникает ошибка при выполнении запроса к базе данных
     */
    private Optional<Student> getStudentById(Long id) {
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
                return Optional.of(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
    /**
     * Получает учителя из базы данных по указанному идентификатору.
     *
     * @param id идентификатор учителя
     * @return учитель с указанным идентификатором, если он найден, или пустой объект, если учитель не найден
     * @throws SQLException если возникает ошибка при выполнении SQL-запроса
     */
    private Optional<Teacher> getTeacherById(Long id) {
        try {
            String query = "SELECT * FROM teachers WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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
            e.printStackTrace();
        }

        return Optional.empty();
    }

}