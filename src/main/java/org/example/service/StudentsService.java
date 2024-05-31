package org.example.service;
import org.example.Dao.StudentDao;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Exceptions.ExistEntityException;
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
    private DBConnector dbConnector = new DBConnector();
    private Connection connection = dbConnector.getConnection();
    private StudentDao studentDao = new StudentDao(connection);


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
    public Map<Integer, List<StudentDto>> getAll() {
        Map<Integer, List<StudentDto>> jsonResponse = new HashMap<>();
        StudentDto studentDto = new StudentDto();
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

    /**
     * Добавляет нового студента.
     *
     * @param studentDto объект DTO студента
     * @return Map JSON-ответа с данными добавленного студента
     * @throws IOException если возникла ошибка при сохранении студента
     */
    public Map<Integer, StudentDto> addStudent(StudentDto studentDto) {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        Student student = StudentMapper.mapStudent.fromDto(studentDto);
        try {
            studentDao.save(student);
        } catch (ExistEntityException e) {
            throw new RuntimeException(e);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        try {
            studentDao.update(student);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
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
            try {
                studentDao.remove(student.get());
            } catch (EntityNotFoundException e) {
                throw new RuntimeException(e);
            }
            jsonResponse = "Student " + id + " removed";
        } else {
            jsonResponse = "Student " + id + " not found";
        }
        return jsonResponse;
    }
}