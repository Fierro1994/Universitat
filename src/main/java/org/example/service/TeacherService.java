package org.example.service;

import org.example.dao.TeacherDao;
import org.example.exceptions.EntityNotFoundException;
import org.example.exceptions.ExistEntityException;
import org.example.dto.TeacherDto;
import org.example.mappers.TeacherMapper;
import org.example.models.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.util.*;


public class TeacherService {
    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);
    private final TeacherDao teacherDao;

    public TeacherService() {
        this.teacherDao = new TeacherDao();
    }

    public TeacherService(TeacherDao teacherDao) {
        this.teacherDao = teacherDao;
    }

    public Map<Integer, TeacherDto> getTeacher(Long id) {
        Map<Integer, TeacherDto> jsonResponse = new HashMap<>();
        TeacherDto teacherDto;
        Optional<Teacher> teacher = teacherDao.getById(id);
        if (teacher.isPresent()) {
            teacherDto = TeacherMapper.mapTeacher.toDto(teacher.get());
            jsonResponse.put(HttpServletResponse.SC_FOUND, teacherDto);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }
        return jsonResponse;
    }

    public Map<Integer, List<TeacherDto>> getAll() {
        Map<Integer, List<TeacherDto>> jsonResponse = new HashMap<>();
        TeacherDto teacherDto;
        Set<Teacher> teacherSet = teacherDao.getAll();
        List<TeacherDto> teacherDtoList = new ArrayList<>();
        for (Teacher teacher : teacherSet) {
            teacherDto = TeacherMapper.mapTeacher.toDto(teacher);
            teacherDtoList.add(teacherDto);
        }
        if (!teacherSet.isEmpty()) {
            jsonResponse.put(HttpServletResponse.SC_FOUND, teacherDtoList);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }

        return jsonResponse;
    }

    public Map<Integer, TeacherDto> addTeacher(TeacherDto teacherDto) {
        Map<Integer, TeacherDto> jsonResponse = new HashMap<>();
        Teacher teacher = TeacherMapper.mapTeacher.fromDto(teacherDto);
        try {
            teacherDao.save(teacher);
        } catch (ExistEntityException | EntityNotFoundException e) {
            logger.error(e.getMessage(), e);
        }

        if (teacher.getId() != null) {
            teacherDto.setId(teacher.getId());
            jsonResponse.put(HttpServletResponse.SC_CREATED, teacherDto);
            return jsonResponse;
        } else {
            jsonResponse.put(HttpServletResponse.SC_CONFLICT, null);
            return jsonResponse;
        }
    }

    public Map<Integer, TeacherDto> updateTeacher(TeacherDto teacherDto) {
        Map<Integer, TeacherDto> jsonResponse = new HashMap<>();
        Teacher teacher = TeacherMapper.mapTeacher.fromDto(teacherDto);
        try {
            teacherDao.update(teacher);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
        teacherDto = TeacherMapper.mapTeacher.toDto(teacher);
        jsonResponse.put(HttpServletResponse.SC_OK, teacherDto);
        return jsonResponse;
    }

    public String removeTeacher(Long id) {
        String jsonResponse = "";
        Optional<Teacher> teacher = teacherDao.getById(id);
        if (teacher.get().getId() != null) {
            try {
                teacherDao.remove(teacher.get());
            } catch (EntityNotFoundException e) {
                logger.error(e.getMessage(), e);
            }
            jsonResponse = "Teacher " + id + " removed";
        } else {
            jsonResponse = "Teacher " + id + " not found";
        }
        return jsonResponse;
    }
}
