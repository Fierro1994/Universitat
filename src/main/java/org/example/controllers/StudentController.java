package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.StudentDto;
import org.example.service.StudentsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
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
                "/getStudent"
        })
public class StudentController extends HttpServlet {

    private final StudentsService studentsService = new StudentsService();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathParam = request.getServletPath();
        Long id = Long.parseLong(request.getParameter("id"));
        StudentDto studentDto = new StudentDto();
        switch (pathParam) {
            case "/getStudent":
                Map<Integer, StudentDto> responseMap = studentsService.getStudent(id);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                studentDto = responseMap.entrySet().stream().findFirst().get().getValue();
        }
        out.println(studentDto);
        out.flush();
        out.close();
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String pathParam = request.getServletPath();
        PrintWriter out = response.getWriter();
        BufferedReader reader = request.getReader();
        request.setCharacterEncoding("UTF-8");
        String json = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper objectMapper = new ObjectMapper();
        StudentDto studentDto = objectMapper.readValue(json, StudentDto.class);
        switch (pathParam) {
            case "/addStudent":
                Map<Integer, StudentDto> responseMap = studentsService.addStudent(studentDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                studentDto = responseMap.entrySet().stream().findFirst().get().getValue();
        }
        out.println(studentDto);
        out.flush();
        out.close();

    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
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
        }
        out.println(studentDto);
        out.flush();
        out.close();

    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathParam = request.getServletPath();
        String jsonResponse = "";
        Long id = Long.parseLong(request.getParameter("id"));
        switch (pathParam) {
            case "/removeStudent":
                jsonResponse = studentsService.removeStudent(id);
        }
        out.println(jsonResponse);
        out.flush();
        out.close();

    }
}




