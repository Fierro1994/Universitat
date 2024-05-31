package org.example.Dao;

import org.example.Dao.interfaceDao.CrudDao;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Exceptions.ExistEntityException;
import org.example.models.Course;
import org.example.models.Teacher;

import java.sql.*;
import java.util.*;
/**
 * Класс для работы с преподавателями в базе данных.
 */
public class TeacherDao implements CrudDao<Teacher> {
    private final String DB_NAME = "dbmelody";
    private Connection connection;
    public TeacherDao (Connection connection){
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
     * Получает преподавателя по его идентификатору.
     *
     * @param id идентификатор преподавателя
     * @return преподаватель, если найден, иначе пустой Optional
     */
    @Override
    public Optional<Teacher> getById(Long id) {
        try {
            String query = "SELECT * FROM teachers WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
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
            e.printStackTrace();
        }
        return Optional.empty();
    }
    /**
     * Получает всех преподавателей.
     *
     * @return set преподавателей
     */
    @Override
    public Set<Teacher> getAll() {
        Set<Teacher> teachers = new HashSet<>();
        try {
            String query = "SELECT * FROM teachers";
            Statement statement = connection.createStatement();
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
            e.printStackTrace();
        }
        return teachers;
    }
    /**
     * Сохраняет преподавателя в базе данных.
     *
     * @param teacher преподаватель для сохранения
     */
    @Override
    public void save(Teacher teacher) throws ExistEntityException {
        Optional<Teacher> existingTeacher = getByEmail(teacher.getEmail());
        if (existingTeacher.isPresent()) {
            throw new ExistEntityException("Teacher with email " + teacher.getEmail() + " already exists");
        } else {
            try {
                String query = "INSERT INTO teachers (name_teacher, email) VALUES (?, ?)";
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
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
                e.printStackTrace();
            } catch (EntityNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Обновляет данные преподателя в базе данных.
     *
     * @param teacher преподатель для обновления
     */
    @Override
    public void update(Teacher teacher) throws EntityNotFoundException {
        CourseDao courseDao = new CourseDao(connection);
        Optional<Teacher> existingTeacher = getByEmail(teacher.getEmail());
        if (!existingTeacher.isPresent()) {
            throw new EntityNotFoundException("Teacher with email " + teacher.getEmail() + " not found");
        } else {
            try {
                String query = "UPDATE teachers SET name_teacher = ? WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, teacher.getName());
                statement.setLong(2, teacher.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (teacher.getCourses() != null) {
                for (Course course : teacher.getCourses()) {
                    Optional<Course> existingCourse = courseDao.getByName(course.getName());
                    if (!existingCourse.isPresent()) {
                        throw new EntityNotFoundException("Course with name " + course.getName() + " not found");
                    } else {
                        updateTeacherCourse(teacher, existingCourse.get());
                    }
                }

            }
        }
        teacher = getByEmail(teacher.getEmail()).get();
    }
    /**
     * Удаляет преподавателя из базы данных.
     *
     * @param teacher преподаватель для удаления
     */
    @Override
    public void remove(Teacher teacher) throws EntityNotFoundException {
        Optional<Teacher> existingTeacher = getByEmail(teacher.getEmail());
        if (!existingTeacher.isPresent()) {
            throw new EntityNotFoundException("Teacher not found");
        } else {
            try {
                String query = "DELETE FROM teachers WHERE id = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setLong(1, teacher.getId());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Получает преподавателя по его email.
     *
     * @param email email преподавателя
     * @return преподаватель, если найден, иначе пустой Optional
     */
    private Optional<Teacher> getByEmail(String email) {
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
     * Получает курсы преподавателя.
     *
     * @param teacherId идентификатор преподавателя
     * @return набор курсов преподавателя
     * @throws SQLException если возникла ошибка при выполнении запроса к базе данных
     */
    private Set<Course> getCoursesForTeacher(Long teacherId) throws SQLException {
        Set<Course> courses = new HashSet<>();
        String query = "SELECT * FROM courses WHERE teacher_id = ?";
        PreparedStatement statement = connection.prepareStatement(query);
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
    }

    /**
     * Получает курс по его имени.
     *
     * @param name имя курса
     * @return курс, если найден, иначе пустой Optional
     * @throws SQLException если возникла ошибка при выполнении запроса к базе данных
     */
    private Optional<Course> getCourseByName(String name) throws SQLException {
        Course course = new Course();
        String query = "SELECT * FROM courses WHERE name_course = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            course.setId(resultSet.getLong("id"));
            course.setName(resultSet.getString("name_course"));
            return Optional.of(course);
        }
        return Optional.empty();
    }
    /**
     * Обновляет преподавателя в курсе.
     *
     * @param teacher преподаватель
     * @param course курс
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

}
