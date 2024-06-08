package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CourseDto;
import org.example.service.CourseService;

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

@WebServlet(name = "CourseController",
        loadOnStartup = 1,
        urlPatterns = {
                "/addCourse",
                "/updateCourse",
                "/removeCourse",
                "/getCourse",
                "/getAllCourses"
        })
public class CourseController extends HttpServlet {
    private static final String JSON_CONTENT_TYPE = "application/json; charset=UTF-8";
    private static final String ENCODING_UTF_8 = "UTF-8";

    private final CourseService courseService;

    public CourseController() {
        this.courseService = new CourseService();
    }

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
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
        CourseDto courseDto;
        List<CourseDto> courseDtoList;
        switch (pathParam) {
            case "/getCourse":
                Map<Integer, CourseDto> responseMap = courseService.getCourse(id);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                courseDto = responseMap.entrySet().stream().findFirst().get().getValue();
                out.print(courseDto.toString());
                out.flush();
                out.close();
                break;
            case "/getAllCourses":
                Map<Integer, List<CourseDto>> responseMapList = courseService.getAll();
                response.setStatus(responseMapList.entrySet().stream().findFirst().get().getKey());
                courseDtoList = responseMapList.entrySet().stream().findFirst().get().getValue();
                out.print(courseDtoList);
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
        CourseDto courseDto = objectMapper.readValue(json, CourseDto.class);

        switch (pathParam) {
            case "/addCourse":
                Map<Integer, CourseDto> responseMap = courseService.addCourse(courseDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                courseDto = responseMap.entrySet().stream().findFirst().get().getValue();
                break;
            default:
                break;
        }
        out.println(courseDto.toString());
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
        CourseDto courseDto = objectMapper.readValue(json, CourseDto.class);
        switch (pathParam) {
            case "/updateCourse":
                Map<Integer, CourseDto> responseMap = courseService.updateCourse(courseDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                courseDto = responseMap.entrySet().stream().findFirst().get().getValue();
                break;
            default:
                break;
        }
        out.println(courseDto);
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
            case "/removeCourse":
                jsonResponse = courseService.removeCourse(id);
                break;
            default:
                break;
        }
        out.print(jsonResponse);
        out.flush();
        out.close();
    }
}




