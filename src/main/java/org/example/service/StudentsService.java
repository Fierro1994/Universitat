package org.example.service;

import org.example.Dao.StudentDao;
import org.example.dto.StudentDto;
import org.example.mappers.StudentMapper;
import org.example.models.Student;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class StudentsService {
    DBConnector dbConnector = new DBConnector();

    private final StudentDao studentDao;
    {
        try {
            studentDao = new StudentDao(dbConnector.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<Integer, StudentDto> getStudent(Long id) {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        StudentDto studentDto = new StudentDto();
        Optional<Student> student = studentDao.getById(id);
        if (student.isPresent()){
            studentDto = StudentMapper.mapStudent.toDto(student.get());
            jsonResponse.put(HttpServletResponse.SC_FOUND, studentDto);
        }else {
            jsonResponse.put(HttpServletResponse.SC_NOT_FOUND, null);
        }
        return jsonResponse;
    }

    public Set<Student> getAll() {
        return studentDao.getAll();
    }

    public Map<Integer, StudentDto> addStudent(StudentDto studentDto) throws IOException {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        Student student = StudentMapper.mapStudent.fromDto(studentDto);
        studentDao.save(student);
        studentDto = StudentMapper.mapStudent.toDto(student);
        jsonResponse.put(HttpServletResponse.SC_CREATED, studentDto);
        return jsonResponse;
    }

    public Map<Integer, StudentDto> updateStudent(StudentDto studentDto) {
        Map<Integer, StudentDto> jsonResponse = new HashMap<>();
        Student student = StudentMapper.mapStudent.fromDto(studentDto);
        studentDao.update(student);
        studentDto = StudentMapper.mapStudent.toDto(student);
        jsonResponse.put(HttpServletResponse.SC_OK, studentDto);
        return jsonResponse;
    }

    public String removeStudent(Long id) {
        String jsonResponse ="";
        Optional<Student>  student = studentDao.getById(id);
        if (student.get().getId() != null) {
            studentDao.remove(student.get());
            jsonResponse = "Student " + id + " removed";
        } else {
            jsonResponse = "Student " + id + " not found";
                  }
        return jsonResponse;
    }
}