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

public class CourseService {

    private Connection connection;
    private CourseDao courseDao;

    public CourseService(Connection connection) {
        this.connection = connection;
        courseDao = new CourseDao(connection);
    }

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

    public Set<Course> getAll() {
        return courseDao.getAll();
    }

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

    public Map<Integer, CourseDto> updateCourse(CourseDto courseDto) {
        Map<Integer, CourseDto> jsonResponse = new HashMap<>();
        Course course = CourseMapper.mapCourse.fromDto(courseDto);
        courseDao.update(course);
        courseDto = CourseMapper.mapCourse.toDto(course);
        jsonResponse.put(HttpServletResponse.SC_OK, courseDto);
        return jsonResponse;
    }

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
