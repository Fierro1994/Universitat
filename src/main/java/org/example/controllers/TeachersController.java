package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.StudentDto;
import org.example.dto.TeacherDto;
import org.example.service.TeacherService;
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

@WebServlet(name = "TeachersController",
        loadOnStartup = 1,
        urlPatterns = {
                "/addTeacher",
                "/updateTeacher",
                "/removeTeacher",
                "/getTeacher",
        })
public class TeachersController extends HttpServlet {

    private final TeacherService teacherService = new TeacherService();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathParam = request.getServletPath();
        TeacherDto teacherDto = new TeacherDto();
        Long id =Long.parseLong(request.getParameter("id"));
        switch (pathParam) {
            case  "/getTeacher":
                Map<Integer, TeacherDto> responseMap = teacherService.getTeacher(id);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                teacherDto = responseMap.entrySet().stream().findFirst().get().getValue();
        }
        out.println(teacherDto);
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
        TeacherDto teacherDto = objectMapper.readValue(json, TeacherDto.class);
        switch (pathParam) {
            case  "/addTeacher":
                Map<Integer, TeacherDto> responseMap = teacherService.addTeacher(teacherDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                teacherDto = responseMap.entrySet().stream().findFirst().get().getValue();
        }
        out.println(teacherDto);
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
        request.setCharacterEncoding("UTF-8");
        String json = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper objectMapper = new ObjectMapper();
        TeacherDto teacherDto = objectMapper.readValue(json, TeacherDto.class);



        switch (pathParam) {
            case  "/updateTeacher":
                Map<Integer, TeacherDto> responseMap = teacherService.updateTeacher(teacherDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                teacherDto= responseMap.entrySet().stream().findFirst().get().getValue();
        }
        out.println(teacherDto);
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
        Long id =Long.parseLong(request.getParameter("id"));
        switch (pathParam) {
            case  "/removeTeacher":
                jsonResponse = teacherService.removeTeacher(id);
        }
        out.println(jsonResponse);
        out.flush();
        out.close();

    }
}




