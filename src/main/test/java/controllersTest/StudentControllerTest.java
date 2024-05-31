package controllersTest;

import org.example.controllers.StudentController;
import org.example.dto.StudentDto;
import org.example.service.StudentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import static org.mockito.Mockito.*;

public class StudentControllerTest {

    @Mock
    private StudentsService studentsService;
    @Mock
    private PrintWriter printWriter;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private StudentController studentController;


    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetControllerStudent() throws IOException {
        Map<Integer, StudentDto> responseMap = new HashMap<>();
        StudentDto studentDto = new StudentDto("1@mail.ru", "Виктор", 30);
        studentDto.setId(1L);
        responseMap.put(HttpServletResponse.SC_FOUND, studentDto);

        when(request.getServletPath()).thenReturn("/getStudent");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(studentsService.getStudent(1L)).thenReturn(responseMap);

        studentController.doGet(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FOUND);
        verify(printWriter, times(1)).print(responseMap.get(HttpServletResponse.SC_FOUND).toString());
    }

    @Test
    void testGetControllerAllStudent() throws IOException {
        Map<Integer, List<StudentDto>> responseMap = new HashMap<>();
        StudentDto studentDto = new StudentDto("1@mail.ru", "Виктор", 30);
        StudentDto studentDto2 = new StudentDto("2mail.ru", "Петр", 30);
        studentDto.setId(1L);
        studentDto2.setId(1L);
        List<StudentDto> studentDtoList = new ArrayList<>();
        studentDtoList.add(studentDto);
        studentDtoList.add(studentDto2);

        responseMap.put(HttpServletResponse.SC_FOUND, studentDtoList);

        when(request.getServletPath()).thenReturn("/getAllStudents");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(studentsService.getAll()).thenReturn(responseMap);

        studentController.doGet(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FOUND);
        verify(printWriter, times(1)).print(responseMap.get(HttpServletResponse.SC_FOUND));
    }

    @Test
    void testDoPost_AddStudent() throws IOException {
        StudentDto studentDto = new StudentDto("2@mail.ru", "Василий", 30);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ObjectMapper objectMapper = new ObjectMapper();

        Map<Integer, StudentDto> responseMap = new HashMap<>();
        responseMap.put(HttpServletResponse.SC_CREATED, studentDto);

        when(request.getServletPath()).thenReturn("/addStudent");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(studentDto))));
        when(response.getWriter()).thenReturn(printWriter);
        when(studentsService.addStudent(any(StudentDto.class))).thenReturn(responseMap);

        studentController.doPost(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    @Test
    void testDoPut_UpdateStudent() throws IOException {
        StudentDto studentDto = new StudentDto("1@mail.ru", "Виктор", 30);
        studentDto.setId(1L);


        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        ObjectMapper objectMapper = new ObjectMapper();

        Map<Integer, StudentDto> responseMap = new HashMap<>();
        responseMap.put(HttpServletResponse.SC_OK, studentDto);

        when(request.getServletPath()).thenReturn("/updateStudent");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(objectMapper.writeValueAsString(studentDto))));
        when(response.getWriter()).thenReturn(printWriter);
        when(studentsService.updateStudent(any(StudentDto.class))).thenReturn(responseMap);

        studentController.doPut(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void testDoRemove_RemoveStudent() throws IOException {
        PrintWriter printWriter = mock(PrintWriter.class);

        when(request.getServletPath()).thenReturn("/removeStudent");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(studentsService.removeStudent(1L)).thenReturn("Student 1 removed");

        studentController.doDelete(request, response);

        verify(printWriter, times(1)).print("Student 1 removed");
    }
}
