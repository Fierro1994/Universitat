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
/**
 * Класс для реализации бизнесс логики преподавателей.
 */
public class TeacherService {
    private final TeacherDao teacherDao;
    private Connection connection;

    public TeacherService(Connection connection) {
        this.connection = connection;
        teacherDao = new TeacherDao(connection);
    }
    /**
     * Метод для получения информации о преподавателе по его идентификатору.
     * @param id - идентификатор преподавателя
     * @return - Map, содержащая статус HTTP и объект DTO преподавателя, если он найден, или null, если преподаватель не найден
     */
    public Map<Integer, TeacherDto> getTeacher(Long id) {
        Map<Integer, TeacherDto> jsonResponse = new HashMap<>();
        TeacherDto teacherDto = new TeacherDto();
        Optional<Teacher> teacher = teacherDao.getById(id);
        if (teacher.isPresent()) {
            teacherDto = TeacherMapper.mapTeacher.toDto(teacher.get());
            jsonResponse.put(HttpServletResponse.SC_FOUND, teacherDto);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }
        return jsonResponse;
    }

    /**
     * Метод для получения всех преподавателей из базы данных.
     * @return - Set объектов преподавателей
     */
    public Set<Teacher> getAll() {
        return teacherDao.getAll();
    }

    /**
     * Метод для добавления нового преподавателя в базу данных.
     * @param teacherDto - объект DTO преподавателя, который нужно добавить
     * @return - Map, содержащая статус HTTP и объект DTO преподавателя, если преподаватель успешно добавлен, или null, если добавление преподавателя не удалось
     * @throws IOException - если при добавлении преподавателя возникает исключение
     */
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

    /**
     * Метод для обновления информации о преподавателе в базе данных.
     * @param teacherDto - объект DTO преподавателя, который нужно обновить
     * @return - Map, содержащая статус HTTP и объект DTO преподавателя, если преподаватель успешно обновлен
     */
    public Map<Integer, TeacherDto> updateTeacher(TeacherDto teacherDto) {
        Map<Integer, TeacherDto> jsonResponse = new HashMap<>();
        Teacher teacher = TeacherMapper.mapTeacher.fromDto(teacherDto);
        teacherDao.update(teacher);
        teacherDto = TeacherMapper.mapTeacher.toDto(teacher);
        jsonResponse.put(HttpServletResponse.SC_OK, teacherDto);
        return jsonResponse;
    }

    /**
     * Метод для удаления преподавателя из базы данных по его идентификатору.
     * @param id - идентификатор преподавателя, который нужно удалить
     * @return - строка с сообщением о результате удаления преподавателя
     */
    public String removeTeacher(Long id) {
        String jsonResponse = "";
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
