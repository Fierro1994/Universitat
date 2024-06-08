package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.TeacherDto;
import org.example.service.TeacherService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@WebServlet(name = "TeachersController",
        loadOnStartup = 1,
        urlPatterns = {
                "/addTeacher",
                "/updateTeacher",
                "/removeTeacher",
                "/getTeacher",
                "/getAllTeachers"
        })
public class TeachersController extends HttpServlet {
    private static final String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
    private static final String ENCODING_UTF_8 = "UTF-8";
    private final TeacherService teacherService;

    public TeachersController() {
        this.teacherService = new TeacherService();
    }

    public TeachersController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }


    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding(ENCODING_UTF_8);
        response.setCharacterEncoding(ENCODING_UTF_8);
        response.setContentType(JSON_CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        String pathParam = request.getServletPath();
        TeacherDto teacherDto;
        Long id = Long.parseLong(request.getParameter("id"));
        List<TeacherDto> teacherDtoList;
        switch (pathParam) {
            case "/getTeacher":
                Map<Integer, TeacherDto> responseMap = teacherService.getTeacher(id);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                teacherDto = responseMap.entrySet().stream().findFirst().get().getValue();
                out.print(teacherDto.toString());
                out.flush();
                out.close();
                break;
            case "/getAllTeachers":
                Map<Integer, List<TeacherDto>> responseMapList = teacherService.getAll();
                response.setStatus(responseMapList.entrySet().stream().findFirst().get().getKey());
                teacherDtoList = responseMapList.entrySet().stream().findFirst().get().getValue();
                out.print(teacherDtoList);
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
        TeacherDto teacherDto = objectMapper.readValue(json, TeacherDto.class);
        switch (pathParam) {
            case "/addTeacher":
                Map<Integer, TeacherDto> responseMap = teacherService.addTeacher(teacherDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                teacherDto = responseMap.entrySet().stream().findFirst().get().getValue();
                break;
            default:
                break;
        }
        out.print(teacherDto.toString());
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
        TeacherDto teacherDto = objectMapper.readValue(json, TeacherDto.class);


        switch (pathParam) {
            case "/updateTeacher":
                Map<Integer, TeacherDto> responseMap = teacherService.updateTeacher(teacherDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                teacherDto = responseMap.entrySet().stream().findFirst().get().getValue();
                break;
            default:
                break;
        }
        out.print(teacherDto.toString());
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
            case "/removeTeacher":
                jsonResponse = teacherService.removeTeacher(id);
                break;
            default:
                break;
        }
        out.print(jsonResponse);
        out.flush();
        out.close();
    }
}




