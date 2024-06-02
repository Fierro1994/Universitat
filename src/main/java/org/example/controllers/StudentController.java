package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.StudentDto;
import org.example.service.StudentsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "StudentsController",
        loadOnStartup = 1,
        urlPatterns = {
                "/addStudent",
                "/updateStudent",
                "/removeStudent",
                "/getStudent",
                "/getAllStudents"
        })
public class StudentController extends HttpServlet {
    private static final String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
    private static final String ENCODING_UTF_8 = "UTF-8";

    private final StudentsService studentsService;

    public StudentController() {
        this.studentsService = new StudentsService();
    }

    public StudentController(StudentsService studentsService) {
        this.studentsService = studentsService;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding(ENCODING_UTF_8);
        response.setCharacterEncoding(ENCODING_UTF_8);
        response.setContentType(JSON_CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        String pathParam = request.getServletPath();
        Long id = Long.parseLong(request.getParameter("id"));
        StudentDto studentDto;
        List<StudentDto> studentDtoList;
        switch (pathParam) {
            case "/getStudent":
                Map<Integer, StudentDto> responseMap = studentsService.getStudent(id);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                studentDto = responseMap.entrySet().stream().findFirst().get().getValue();
                out.print(studentDto.toString());
                out.flush();
                out.close();
                break;
            case "/getAllStudents":
                Map<Integer, List<StudentDto>> responseMapList = studentsService.getAll();
                response.setStatus(responseMapList.entrySet().stream().findFirst().get().getKey());
                studentDtoList = responseMapList.entrySet().stream().findFirst().get().getValue();
                out.print(studentDtoList);
                out.flush();
                out.close();
                break;
            default:
                break;
        }

    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding(ENCODING_UTF_8);
        response.setCharacterEncoding(ENCODING_UTF_8);
        response.setContentType(JSON_CONTENT_TYPE);
        String pathParam = request.getServletPath();
        PrintWriter out = response.getWriter();
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper objectMapper = new ObjectMapper();
        StudentDto studentDto = objectMapper.readValue(json, StudentDto.class);
        switch (pathParam) {
            case "/addStudent":
                Map<Integer, StudentDto> responseMap = studentsService.addStudent(studentDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                studentDto = responseMap.entrySet().stream().findFirst().get().getValue();
                break;
            default:
                break;
        }
        out.println(studentDto);
        out.flush();
        out.close();

    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(ENCODING_UTF_8);
        response.setCharacterEncoding(ENCODING_UTF_8);
        response.setContentType(JSON_CONTENT_TYPE);
        String pathParam = request.getServletPath();
        PrintWriter out = response.getWriter();
        BufferedReader reader = request.getReader();
        String json = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper objectMapper = new ObjectMapper();
        StudentDto studentDto = objectMapper.readValue(json, StudentDto.class);
        switch (pathParam) {
            case "/updateStudent":
                Map<Integer, StudentDto> responseMap = studentsService.updateStudent(studentDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                studentDto = responseMap.entrySet().stream().findFirst().get().getValue();
                break;
            default:
                break;
        }
        out.println(studentDto);
        out.flush();
        out.close();
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(ENCODING_UTF_8);
        response.setCharacterEncoding(ENCODING_UTF_8);
        response.setContentType(JSON_CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        String pathParam = request.getServletPath();
        String jsonResponse = "";
        Long id = Long.parseLong(request.getParameter("id"));
        switch (pathParam) {
            case "/removeStudent":
                jsonResponse = studentsService.removeStudent(id);
                break;
            default:
                break;
        }
        out.print(jsonResponse);
        out.flush();
        out.close();
    }
}




