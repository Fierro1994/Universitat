package org.example.service;

import org.example.Dao.StudentDao;
import org.example.dto.StudentDto;
import org.example.mappers.StudentMapper;
import org.example.models.Student;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.*;
/**
 * Класс для реализации бизнесс логики студентов.
 */
public class StudentsService {
    private Connection connection;

    private final StudentDao studentDao;

    /**
     * Создает новый экземпляр класса StudentsService.
     *
     * @param connection подключение к базе данных
     */
    public StudentsService(Connection connection) {
        this.connection = connection;
        studentDao = new StudentDao(connection);
    }
    /**
     * Получает студента по его идентификатору.
     *
     * @param id идентификатор студента
     * @return Map JSON-ответа с данными студента
     */
    public Map<Integer, StudentDto> getStudent(Long id) {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        StudentDto studentDto = new StudentDto();
        Optional<Student> student = studentDao.getById(id);
        if (student.isPresent()) {
            studentDto = StudentMapper.mapStudent.toDto(student.get());
            jsonResponse.put(HttpServletResponse.SC_FOUND, studentDto);
        } else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }
        return jsonResponse;
    }

    /**
     * Получает всех студентов.
     *
     * @return множество объектов студентов
     */
    public Set<Student> getAll() {
        return studentDao.getAll();
    }

    /**
     * Добавляет нового студента.
     *
     * @param studentDto объект DTO студента
     * @return Map JSON-ответа с данными добавленного студента
     * @throws IOException если возникла ошибка при сохранении студента
     */
    public Map<Integer, StudentDto> addStudent(StudentDto studentDto) throws IOException {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        Student student = StudentMapper.mapStudent.fromDto(studentDto);
        studentDao.save(student);
        studentDto = StudentMapper.mapStudent.toDto(student);
        jsonResponse.put(HttpServletResponse.SC_CREATED, studentDto);
        return jsonResponse;
    }
    /**
     * Обновляет данные студента.
     *
     * @param studentDto объект DTO студента
     * @return Map JSON-ответа с данными обновленного студента
     */
    public Map<Integer, StudentDto> updateStudent(StudentDto studentDto) {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        Student student = StudentMapper.mapStudent.fromDto(studentDto);
        studentDao.update(student);
        studentDto = StudentMapper.mapStudent.toDto(student);
        jsonResponse.put(HttpServletResponse.SC_OK, studentDto);
        return jsonResponse;
    }
    /**
     * Удаляет студента по его идентификатору.
     *
     * @param id идентификатор студента
     * @return строка JSON-ответа с сообщением о результате операции
     */
    public String removeStudent(Long id) {
        String jsonResponse = "";
        Optional<Student> student = studentDao.getById(id);
        if (student.get().getId() != null) {
            studentDao.remove(student.get());
            jsonResponse = "Student " + id + " removed";
        } else {
            jsonResponse = "Student " + id + " not found";
        }
        return jsonResponse;
    }
}