
import org.example.controllers.CourseController;
import org.example.dto.CourseDto;
import org.example.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.Mockito.*;
class ControllersTest {


    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testDoGet() throws Exception {
//        courseController = new CourseController(connection);
        // Set up mock objects and test data
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter printWriter = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(printWriter);
        when(request.getServletPath()).thenReturn("/getCourse");
        when(request.getParameter("id")).thenReturn("1");

        // Call the method being teste
        verify(response).setStatus(anyInt());
        verify(printWriter).println(any(CourseDto.class));
        verify(printWriter).flush();
        verify(printWriter).close();
    }

    @Test
    void testDoPost_AddCourse() throws ServletException, IOException {
        // Создаем мок для CourseDto
        CourseDto courseDto = new CourseDto("Тестовый курс");

        // Создаем мок для HttpServletRequest с ожидаемым JSON-полезным нагрузом
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getServletPath()).thenReturn("/addCourse");
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(getJsonPayload(courseDto))));

        // Создаем мок для HttpServletResponse
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        // Создаем мок для CourseService и моделируем поведение метода addCourse()
        CourseService courseService = mock(CourseService.class);
        Map<Integer, CourseDto> responseMap = new HashMap<>();
        responseMap.put(HttpServletResponse.SC_CREATED, courseDto);
        when(courseService.addCourse(any(CourseDto.class))).thenReturn(responseMap);

        // Создаем экземпляр CourseController и устанавливаем мок для courseService
        CourseController courseController = new CourseController();

        // Вызываем метод, который проверяется
        courseController.doPost(request, response);

        // Проверяем ожидаемое поведение
        verify(response, times(1)).setStatus(HttpServletResponse.SC_CREATED);
    }

    private String getJsonPayload(CourseDto courseDto) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(courseDto);
    }

    // Add more test methods for other HTTP methods if needed
}