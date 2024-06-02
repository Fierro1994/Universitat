package controllersTest;

import org.example.controllers.TeachersController;
import org.example.dto.TeacherDto;
import org.example.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class TeacherControllerTest {

    @Mock
    private TeacherService teacherService;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TeachersController teachersController;


    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetControllerTeacher() throws IOException {
        Map<Integer, TeacherDto> responseMap = new HashMap<>();
        TeacherDto teacherDto = new TeacherDto("t2@mail.ru", "Василий Петрович");
        teacherDto.setId(1L);
        responseMap.put(HttpServletResponse.SC_FOUND, teacherDto);

        when(request.getServletPath()).thenReturn("/getTeacher");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(teacherService.getTeacher(1L)).thenReturn(responseMap);

        teachersController.doGet(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FOUND);
        verify(printWriter, times(1)).print(responseMap.get(HttpServletResponse.SC_FOUND).toString());
    }

    @Test
    void testGetControllerAllCourses() throws IOException {
        Map<Integer, List<TeacherDto>> responseMap = new HashMap<>();
        TeacherDto teacherDto = new TeacherDto("t1@mail.ru", "Василий Петрович");
        TeacherDto teacherDto2 = new TeacherDto("t2@mail.ru", "Иван Прокофьев");
        teacherDto.setId(1L);
        teacherDto2.setId(2L);
        List<TeacherDto> teacherDtoList = new ArrayList<>();
        teacherDtoList.add(teacherDto);
        teacherDtoList.add(teacherDto2);

        responseMap.put(HttpServletResponse.SC_FOUND, teacherDtoList);

        when(request.getServletPath()).thenReturn("/getAllTeachers");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(teacherService.getAll()).thenReturn(responseMap);

        teachersController.doGet(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FOUND);
        verify(printWriter, times(1)).print(responseMap.get(HttpServletResponse.SC_FOUND));
    }

    @Test
    void testDoPost_AddCourse() throws IOException {
        TeacherDto teacherDto = new TeacherDto("t1@mail.ru", "Василий Петрович");
        StringWriter stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        ObjectMapper objectMapper = new ObjectMapper();

        Map<Integer, TeacherDto> responseMap = new HashMap<>();
        responseMap.put(HttpServletResponse.SC_CREATED, teacherDto);

        when(request.getServletPath()).thenReturn("/addTeacher");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(teacherDto))));
        when(response.getWriter()).thenReturn(printWriter);
        when(teacherService.addTeacher(any(TeacherDto.class))).thenReturn(responseMap);

        teachersController.doPost(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void testDoPut_UpdateCourse() throws IOException {
        TeacherDto teacherDto = new TeacherDto("t1@mail.ru", "Василий Петрович");
        teacherDto.setId(1L);
        StringWriter stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        ObjectMapper objectMapper = new ObjectMapper();

        Map<Integer, TeacherDto> responseMap = new HashMap<>();
        responseMap.put(HttpServletResponse.SC_OK, teacherDto);

        when(request.getServletPath()).thenReturn("/updateTeacher");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(teacherDto))));
        when(response.getWriter()).thenReturn(printWriter);
        when(teacherService.updateTeacher(any(TeacherDto.class))).thenReturn(responseMap);

        teachersController.doPut(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoRemove_RemoveCourse() throws IOException {
        printWriter = mock(PrintWriter.class);

        when(request.getServletPath()).thenReturn("/removeTeacher");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(teacherService.removeTeacher(1L)).thenReturn("Teacher 1 removed");

        teachersController.doDelete(request, response);

        verify(printWriter, times(1)).print("Teacher 1 removed");
    }


}
