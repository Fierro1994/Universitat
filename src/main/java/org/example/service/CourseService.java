package org.example.service;

import org.example.dao.CourseDao;
import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.ExistEntityException;
import org.example.dto.CourseDto;
import org.example.mappers.CourseMapper;
import org.example.models.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

public class CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    private final CourseDao courseDao;

    public CourseService() {
        this.courseDao = new CourseDao();
    }

    public CourseService(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    public Map<Integer, CourseDto> getCourse(Long id) {
        Map<Integer, CourseDto> jsonResponse = new HashMap<>();
        CourseDto courseDto;
        Optional<Course> course = courseDao.getById(id);
        if (course.isPresent()) {
            courseDto = CourseMapper.mapCourse.toDto(course.get());
            jsonResponse.put(HttpServletResponse.SC_FOUND, courseDto);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }
        return jsonResponse;
    }

    public Map<Integer, List<CourseDto>> getAll() {
        Map<Integer, List<CourseDto>> jsonResponse = new HashMap<>();
        CourseDto courseDto;
        Set<Course> courseSet = courseDao.getAll();
        List<CourseDto> courseDtoList = new ArrayList<>();
        for (Course course : courseSet) {
            courseDto = CourseMapper.mapCourse.toDto(course);
            courseDtoList.add(courseDto);
        }
        if (!courseSet.isEmpty()) {
            jsonResponse.put(HttpServletResponse.SC_FOUND, courseDtoList);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }

        return jsonResponse;
    }

    public Map<Integer, CourseDto> addCourse(CourseDto courseDto) {
        Map<Integer, CourseDto> jsonResponse = new HashMap<>();
        Course course = CourseMapper.mapCourse.fromDto(courseDto);
        try {
            courseDao.save(course);
        } catch (ExistEntityException | EntityNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
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
        try {
            courseDao.update(course);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        courseDto = CourseMapper.mapCourse.toDto(course);
        jsonResponse.put(HttpServletResponse.SC_OK, courseDto);
        return jsonResponse;
    }

    public String removeCourse(Long id) {
        String jsonResponse = "";
        Optional<Course> course = courseDao.getById(id);
        if (course.get().getId() != null) {
            try {
                courseDao.remove(course.get());
            } catch (EntityNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
            jsonResponse = "Course " + id + " removed";
        } else {
            jsonResponse = "Course " + id + " not found";
        }
        return jsonResponse;
    }
}
