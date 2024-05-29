package org.example.service;

import org.example.Dao.TeacherDao;
import org.example.dto.TeacherDto;
import org.example.mappers.TeacherMapper;
import org.example.models.Teacher;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TeacherService {
    private final TeacherDao teacherDao;
    private Connection connection;
    public TeacherService(Connection connection) {
        this.connection = connection;
        teacherDao = new TeacherDao(connection);
    }

    public Map<Integer, TeacherDto> getTeacher(Long id) {
        Map<Integer, TeacherDto> jsonResponse = new HashMap<>();
        TeacherDto teacherDto = new TeacherDto();
        Optional<Teacher>  teacher = teacherDao.getById(id);
        if (teacher.isPresent()){
            teacherDto = TeacherMapper.mapTeacher.toDto(teacher.get());
            jsonResponse.put(HttpServletResponse.SC_FOUND, teacherDto);
        }else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }
        return jsonResponse;
    }

    public Set<Teacher> getAll() {
        return teacherDao.getAll();
    }

    public Map<Integer, TeacherDto> addTeacher(TeacherDto teacherDto) throws IOException {
        Map<Integer, TeacherDto> jsonResponse = new HashMap<>();
        Teacher teacher = TeacherMapper.mapTeacher.fromDto(teacherDto);
        teacherDao.save(teacher);

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
        teacherDao.update(teacher);
        teacherDto = TeacherMapper.mapTeacher.toDto(teacher);
        jsonResponse.put(HttpServletResponse.SC_OK, teacherDto);
        return jsonResponse;
    }

    public String removeTeacher(Long id) {
        String jsonResponse ="";
        Optional<Teacher> teacher = teacherDao.getById(id);
        if (teacher.get().getId() != null) {
            teacherDao.remove(teacher.get());
            jsonResponse = "Teacher " + id + " removed";
        } else {
            jsonResponse = "Teacher " + id + " not found";
        }
        return jsonResponse;
    }

}
