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


/**
 * Контроллер студентов.
 * Этот класс обрабатывает HTTP-запросы для работы с студентами.
 * Он использует сервис StudentsService для выполнения бизнес-логики.
 */
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

    private StudentsService studentsService = new StudentsService();

    /**
     * Обработка запроса GET.
     * @param request запрос
     * @param response ответ
     * @throws IOException исключение
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
        }

    }

    /**
     * Обработка запроса POST.
     * @param request запрос
     * @param response ответ
     * @throws IOException исключение
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
    /**
     * Обработка запроса PUT.
     * @param request запрос
     * @param response ответ
     * @throws IOException исключение
     */
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            case "/removeStudent":
                jsonResponse = studentsService.removeStudent(id);
        }
        out.print(jsonResponse);
        out.flush();
        out.close();

    }
}




