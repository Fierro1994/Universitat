package controllersTest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.controllers.CourseController;
import org.example.dto.CourseDto;
import org.example.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

class CourseControllerTest {

    @Mock
    private CourseService courseService;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private CourseController courseController;

    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetControllerCourse() throws IOException {

        Map<Integer, CourseDto> responseMap = new HashMap<>();
        CourseDto courseDto = new CourseDto("java");
        courseDto.setId(1L);
        responseMap.put(HttpServletResponse.SC_FOUND, courseDto);

        when(request.getServletPath()).thenReturn("/getCourse");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(courseService.getCourse(1L)).thenReturn(responseMap);

        courseController.doGet(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FOUND);
        verify(printWriter, times(1)).print(responseMap.get(HttpServletResponse.SC_FOUND).toString());
    }

    @Test
    void testGetControllerAllCourses() throws IOException {
        Map<Integer, List<CourseDto>> responseMap = new HashMap<>();
        CourseDto courseDto = new CourseDto("java");
        CourseDto courseDto2 = new CourseDto("js");
        courseDto.setId(1L);
        courseDto2.setId(1L);
        List<CourseDto> courseDtoList = new ArrayList<>();
        courseDtoList.add(courseDto);
        courseDtoList.add(courseDto2);

        responseMap.put(HttpServletResponse.SC_FOUND, courseDtoList);

        when(request.getServletPath()).thenReturn("/getAllCourses");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(courseService.getAll()).thenReturn(responseMap);

        courseController.doGet(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FOUND);
        verify(printWriter, times(1)).print(responseMap.get(HttpServletResponse.SC_FOUND));
    }

    @Test
    void testDoPost_AddCourse() throws IOException {
        CourseDto courseDto = new CourseDto("java");
        StringWriter stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        ObjectMapper objectMapper = new ObjectMapper();

        Map<Integer, CourseDto> responseMap = new HashMap<>();
        responseMap.put(HttpServletResponse.SC_CREATED, courseDto);

        when(request.getServletPath()).thenReturn("/addCourse");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(courseDto))));
        when(response.getWriter()).thenReturn(printWriter);
        when(courseService.addCourse(any(CourseDto.class))).thenReturn(responseMap);

        courseController.doPost(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void testDoPut_UpdateCourse() throws IOException {
        CourseDto courseDto = new CourseDto("java");
        courseDto.setId(1L);
        StringWriter stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);
        ObjectMapper objectMapper = new ObjectMapper();

        Map<Integer, CourseDto> responseMap = new HashMap<>();
        responseMap.put(HttpServletResponse.SC_OK, courseDto);

        when(request.getServletPath()).thenReturn("/updateCourse");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(courseDto))));
        when(response.getWriter()).thenReturn(printWriter);
        when(courseService.updateCourse(any(CourseDto.class))).thenReturn(responseMap);

        courseController.doPut(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoRemove_RemoveCourse() throws IOException {
        printWriter = mock(PrintWriter.class);

        when(request.getServletPath()).thenReturn("/removeCourse");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(courseService.removeCourse(1L)).thenReturn("Course 1 removed");

        courseController.doDelete(request, response);

        verify(printWriter, times(1)).print("Course 1 removed");
    }


}
