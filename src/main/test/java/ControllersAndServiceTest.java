
import org.example.Dao.CourseDao;
import org.example.Dao.StudentDao;
import org.example.Dao.TeacherDao;
import org.example.controllers.CourseController;
import org.example.controllers.StudentController;
import org.example.controllers.TeachersController;
import org.example.dto.CourseDto;
import org.example.dto.StudentDto;
import org.example.dto.TeacherDto;
import org.example.models.Course;
import org.example.models.Student;
import org.example.models.Teacher;
import org.example.service.CourseService;
import org.example.service.CreateDBAndDTO;
import org.example.service.StudentsService;
import org.example.service.TeacherService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static org.mockito.Mockito.*;

@Testcontainers
class ControllersAndServiceTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    HttpServletResponse response;
    @Mock
    CourseService courseService;
    @Mock
    StudentsService studentsService;
    @Mock
    TeacherService teacherService;

    private static Connection connection;

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer().withDatabaseName("dbmelody");

    private static CreateDBAndDTO createDBAndDTO;
    private StudentController studentController;
    private CourseController courseController;
    private TeachersController teachersController;

    @BeforeAll
    public static void setUp() throws SQLException {
        MY_SQL_CONTAINER.start();
        connection = DriverManager.getConnection(MY_SQL_CONTAINER.getJdbcUrl(), MY_SQL_CONTAINER.getUsername(), MY_SQL_CONTAINER.getPassword());
        createDBAndDTO = new CreateDBAndDTO(connection);
        createDBAndDTO.createDataBase();

    }

    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
        createDBAndDTO = new CreateDBAndDTO(connection);
        createDBAndDTO.createDataBase();
        Student student = new Student("1@mail.ru", "Виктор", 30);
        StudentDao studentDao = new StudentDao(connection);
        studentDao.save(student);
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        TeacherDao teacherDao = new TeacherDao(connection);
        teacherDao.save(teacher);
        Course course = new Course("java");
        CourseDao courseDao = new CourseDao(connection);
        courseDao.save(course);
        studentController = new StudentController(connection);
        courseController = new CourseController(connection);
        teachersController = new TeachersController(connection);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        connection.close();
    }

    //students controller test
    @Test
    void testDoGetStudent() throws Exception {
        PrintWriter printWriter = mock(PrintWriter.class);
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
        Course course = new Course("js");
        Course course2 = new Course("python");
        CourseDao courseDao = new CourseDao(connection);
        courseDao.save(course);
        courseDao.save(course2);
        Set<Course> courses = new HashSet<>();
        courses.add(course);
        courses.add(course2);
        studentDto.setCourses(courses);

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
        when(courseService.removeCourse(1L)).thenReturn("Student 1 removed");

        studentController.doDelete(request, response);

        verify(printWriter, times(1)).print("Student 1 removed");
    }

    // course controller test

    @Test
    void testDoGetCourse() throws Exception {
        PrintWriter printWriter = mock(PrintWriter.class);
        Map<Integer, CourseDto> courses = new HashMap<>();
        CourseDto courseDto = new CourseDto("java");
        courseDto.setId(1L);
        courses.put(HttpServletResponse.SC_FOUND, courseDto);

        when(request.getServletPath()).thenReturn("/getCourse");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(courseService.getCourse(1L)).thenReturn(courses);

        courseController.doGet(request, response);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FOUND);
        verify(printWriter, times(1)).print(courses.get(HttpServletResponse.SC_FOUND).toString());
    }

    @Test
    void testDoPost_AddCourse() throws IOException {
        CourseDto courseDto = new CourseDto("js");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
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
        Teacher teacher = new Teacher("1t@mail.ru", "Виктор Петрович");
        TeacherDao teacherDao = new TeacherDao(connection);
        teacherDao.save(teacher);
        courseDto.setTeacher(teacher);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
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
        PrintWriter printWriter = mock(PrintWriter.class);

        when(request.getServletPath()).thenReturn("/removeCourse");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(courseService.removeCourse(1L)).thenReturn("Course 1 removed");

        courseController.doDelete(request, response);

        verify(printWriter, times(1)).print("Course 1 removed");
    }

    ///teacher controller test
    @Test
    void testDoGetTeacher() throws Exception {
        PrintWriter printWriter = mock(PrintWriter.class);
        Map<Integer, TeacherDto> responseMap = new HashMap<>();
        TeacherDto teacherDto = new TeacherDto("t1@mail.ru", "Василий Петрович");
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
    void testDoPost_AddTeacher() throws IOException {
        TeacherDto teacherDto = new TeacherDto("t2@mail.ru", "Сергей Петрович");
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
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
    void testDoPut_UpdateTeacher() throws IOException {
        TeacherDto teacherDto = new TeacherDto("t1@mail.ru", "Василий Петрович");
        teacherDto.setId(1L);
        Course course = new Course("js");
        Course course2 = new Course("python");
        CourseDao courseDao = new CourseDao(connection);
        courseDao.save(course);
        courseDao.save(course2);
        Set<Course> courses = new HashSet<>();
        courses.add(course);
        courses.add(course2);
        teacherDto.setCourses(courses);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
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
    void testDoRemove_RemoveTeacher() throws IOException {
        PrintWriter printWriter = mock(PrintWriter.class);

        when(request.getServletPath()).thenReturn("/removeTeacher");
        when(request.getParameter("id")).thenReturn("1");
        when(response.getWriter()).thenReturn(printWriter);
        when(courseService.removeCourse(1L)).thenReturn("Teacher 1 removed");

        teachersController.doDelete(request, response);

        verify(printWriter, times(1)).print("Teacher 1 removed");
    }


}