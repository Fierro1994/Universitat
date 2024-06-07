package org.example.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.dao.StudentDao;
import org.example.dto.CourseDto;
import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.ExistEntityException;
import org.example.dto.StudentDto;
import org.example.mappers.CourseMapper;
import org.example.mappers.StudentMapper;
import org.example.models.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class StudentsService {
    private static final Logger logger = LoggerFactory.getLogger(StudentsService.class);
    private final StudentDao studentDao;

    public StudentsService() {
        this.studentDao = new StudentDao();
    }

    public StudentsService(StudentDao studentDao) {
        this.studentDao = studentDao;
    }

    public Map<Integer, StudentDto> getStudent(Long id) {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        StudentDto studentDto;
        Optional<Student> student = studentDao.getById(id);
        if (student.isPresent()) {
            Set<CourseDto> courseDtos = new HashSet<>();
            student.get().getCourses().forEach(course -> {
                CourseDto courseDto = CourseMapper.mapCourse.toDto(course);
                courseDtos.add(courseDto);
            });
            studentDto = StudentMapper.mapStudent.toDto(student.get());
            studentDto.setCourses(courseDtos);
            jsonResponse.put(HttpServletResponse.SC_FOUND, studentDto);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }

        return jsonResponse;
    }

    public Map<Integer, List<StudentDto>> getAll() {
        Map<Integer, List<StudentDto>> jsonResponse = new HashMap<>();
        StudentDto studentDto;
        Set<Student> studentList = studentDao.getAll();
        List<StudentDto> studentDtoList = new ArrayList<>();
        for (Student student : studentList) {
            studentDto = StudentMapper.mapStudent.toDto(student);
            studentDtoList.add(studentDto);
        }
        if (!studentList.isEmpty()) {
            jsonResponse.put(HttpServletResponse.SC_FOUND, studentDtoList);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }

        return jsonResponse;
    }

    public Map<Integer, StudentDto> addStudent(StudentDto studentDto) {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        Student student = StudentMapper.mapStudent.fromDto(studentDto);
        try {
            studentDao.save(student);
        } catch (ExistEntityException | EntityNotFoundException e) {
            logger.error(e.getMessage());
        }
        studentDto = StudentMapper.mapStudent.toDto(student);
        jsonResponse.put(HttpServletResponse.SC_CREATED, studentDto);
        return jsonResponse;
    }

    public Map<Integer, StudentDto> updateStudent(StudentDto studentDto) {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        Student student = StudentMapper.mapStudent.fromDto(studentDto);
        try {
            studentDao.update(student);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
        }
        studentDto = StudentMapper.mapStudent.toDto(student);
        jsonResponse.put(HttpServletResponse.SC_OK, studentDto);
        return jsonResponse;
    }

    public String removeStudent(Long id) {
        String jsonResponse;
        Optional<Student> student = studentDao.getById(id);
        if (student.get().getId() != null) {
            try {
                studentDao.remove(student.get());
            } catch (EntityNotFoundException e) {
                logger.error(e.getMessage());
            }
            jsonResponse = "Student " + id + " removed";
        } else {
            jsonResponse = "Student " + id + " not found";
        }
        return jsonResponse;
    }
}