package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.CourseDto;
import org.example.service.CourseService;
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

/**
 * Контроллер курсов.
 * Этот класс обрабатывает HTTP-запросы для работы с курсами.
 * Он использует сервис CourseService для выполнения бизнес-логики.
 */

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
    private CourseService courseService = new CourseService();

    /**
     * Обработчик GET-запроса.
     * @param request объект запроса
     * @param response объект ответа
     * @throws IOException исключение ввода-вывода
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
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
        }
    }

    /**
     * Обработчик POST-запроса.
     * @param request объект запроса
     * @param response объект ответа
     * @throws IOException исключение ввода-вывода
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String pathParam = request.getServletPath();
        PrintWriter out = response.getWriter();
        BufferedReader reader = request.getReader();
        request.setCharacterEncoding("UTF-8");
        String json = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper objectMapper = new ObjectMapper();
        CourseDto courseDto = objectMapper.readValue(json, CourseDto.class);

        switch (pathParam) {
            case "/addCourse":
                Map<Integer, CourseDto> responseMap = courseService.addCourse(courseDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                courseDto = responseMap.entrySet().stream().findFirst().get().getValue();
                break;
        }
        out.println(courseDto.toString());
        out.flush();
        out.close();
    }
    /**
     * Обработчик PUT-запроса.
     * @param request объект запроса
     * @param response объект ответа
     * @throws IOException исключение ввода-вывода
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        String pathParam = request.getServletPath();
        PrintWriter out = response.getWriter();
        BufferedReader reader = request.getReader();
        request.setCharacterEncoding("UTF-8");
        String json = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper objectMapper = new ObjectMapper();
        CourseDto courseDto = objectMapper.readValue(json, CourseDto.class);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        switch (pathParam) {
            case "/updateCourse":
                Map<Integer, CourseDto> responseMap = courseService.updateCourse(courseDto);
                response.setStatus(responseMap.entrySet().stream().findFirst().get().getKey());
                courseDto = responseMap.entrySet().stream().findFirst().get().getValue();
                break;
        }
        out.println(courseDto);
        out.flush();
        out.close();
    }
    /**
     * Обработчик DELETE-запроса.
     * @param request объект запроса
     * @param response объект ответа
     * @throws IOException исключение ввода-вывода
     */
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String pathParam = request.getServletPath();
        String jsonResponse = "";
        Long id = Long.parseLong(request.getParameter("id"));
        switch (pathParam) {
            case "/removeCourse":
                jsonResponse = courseService.removeCourse(id);
                break;
        }
        out.print(jsonResponse);
        out.flush();
        out.close();
    }
}




