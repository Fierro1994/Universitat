package org.example.service;

import org.example.Dao.CourseDao;
import org.example.dto.CourseDto;
import org.example.mappers.CourseMapper;
import org.example.models.Course;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
/**
 * Класс для реализации бизнесс логики курсов.
 */
public class CourseService {

    private Connection connection;
    private CourseDao courseDao;

    /**
     * Создает новый экземпляр CourseService.
     *
     * @param connection подключение к базе данных
     */
    public CourseService(Connection connection) {
        this.connection = connection;
        courseDao = new CourseDao(connection);
    }
    /**
     * Получает курс по его идентификатору.
     *
     * @param id идентификатор курса
     * @return набор с информацией о курсе и его статусом (SC_FOUND или SC_NOT_FOUND)
     */
    public Map<Integer, CourseDto> getCourse(Long id) {
        Map<Integer, CourseDto> jsonResponse = new HashMap<>();
        CourseDto courseDto = new CourseDto();
        Optional<Course> course = courseDao.getById(id);
        if (course.isPresent()) {
            courseDto = CourseMapper.mapCourse.toDto(course.get());
            jsonResponse.put(HttpServletResponse.SC_FOUND, courseDto);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }
        return jsonResponse;
    }

    /**
     * Получает все курсы.
     *
     * @return набор курсов
     */
    public Set<Course> getAll() {
        return courseDao.getAll();
    }

    /**
     * Добавляет новый курс.
     *
     * @param courseDto объект CourseDto с информацией о курсе
     * @return Map с информацией о добавленном курсе и его статусом (SC_CREATED или SC_CONFLICT)
     * @throws IOException если возникла ошибка при работе с базой данных
     */
    public Map<Integer, CourseDto> addCourse(CourseDto courseDto) throws IOException {
        Map<Integer, CourseDto> jsonResponse = new HashMap<>();
        Course course = CourseMapper.mapCourse.fromDto(courseDto);
        courseDao.save(course);
        if (course.getId() != null) {
            courseDto.setId(course.getId());
            jsonResponse.put(HttpServletResponse.SC_CREATED, courseDto);
            return jsonResponse;
        } else {
            jsonResponse.put(HttpServletResponse.SC_CONFLICT, null);
            return jsonResponse;
        }
    }
    /**
     * Обновляет информацию о курсе.
     *
     * @param courseDto объект CourseDto с информацией о курсе
     * @return Map с информацией об обновленном курсе и его статусом (SC_OK)
     */
    public Map<Integer, CourseDto> updateCourse(CourseDto courseDto) {
        Map<Integer, CourseDto> jsonResponse = new HashMap<>();
        Course course = CourseMapper.mapCourse.fromDto(courseDto);
        courseDao.update(course);
        courseDto = CourseMapper.mapCourse.toDto(course);
        jsonResponse.put(HttpServletResponse.SC_OK, courseDto);
        return jsonResponse;
    }
    /**
     * Удаляет курс по его идентификатору.
     *
     * @param id идентификатор курса
     * @return JSON-ответ с информацией о результате удаления курса (успешное удаление или сообщение о том, что курс не найден)
     */
    public String removeCourse(Long id) {
        String jsonResponse = "";
        Optional<Course> course = courseDao.getById(id);
        if (course.get().getId() != null) {
            courseDao.remove(course.get());
            jsonResponse = "Course " + id + " removed";
        } else {
            jsonResponse = "Course " + id + " not found";
        }
        return jsonResponse;
    }
}
