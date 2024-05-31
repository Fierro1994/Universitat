package serviceTest;

import org.example.Dao.CourseDao;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Exceptions.ExistEntityException;
import org.example.dto.CourseDto;
import org.example.mappers.CourseMapper;
import org.example.models.Course;
import org.example.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CourseServiceTest {
    @Mock
    private CourseDao courseDao;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetCourse() {
        Course course = new Course("java");
        Optional<Course> optionalCourse = Optional.of(course);

        when(courseDao.getById(1L)).thenReturn(optionalCourse);

        Map<Integer, CourseDto> result = courseService.getCourse(1L);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testGetCourse_NotFound() {
        Optional<Course> optionalCourse = Optional.empty();

        when(courseDao.getById(1L)).thenReturn(optionalCourse);

        Map<Integer, CourseDto> result = courseService.getCourse(1L);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testGetAllCourse() {
        Course course = new Course("java");
        Course course2 = new Course("js");
        Set<Course> courseSet = new HashSet<>();
        courseSet.add(course);
        courseSet.add(course2);

        when(courseDao.getAll()).thenReturn(courseSet);

        Map<Integer, List<CourseDto>> result = courseService.getAll();

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testGetAllCourses_Not_found() {
        Set<Course> courseSet = new HashSet<>();

        when(courseDao.getAll()).thenReturn(courseSet);

        Map<Integer, List<CourseDto>> result = courseService.getAll();

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testAddCourse() throws ExistEntityException, EntityNotFoundException {
        CourseDto courseDto = new CourseDto("java");
        courseDto.setId(1L);
        Course course = CourseMapper.mapCourse.fromDto(courseDto);

        doNothing().when(courseDao).save(course);

        Map<Integer, CourseDto> result = courseService.addCourse(courseDto);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_CREATED, result.keySet().iterator().next());
    }

    @Test
    void testUpdateCourse() throws EntityNotFoundException {
        CourseDto courseDto = new CourseDto("java");
        Course course = CourseMapper.mapCourse.fromDto(courseDto);

        doNothing().when(courseDao).update(course);

        Map<Integer, CourseDto> result = courseService.updateCourse(courseDto);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_OK, result.keySet().iterator().next());
    }

    @Test
    void testRemoveCourse() throws IOException {

        Course course = new Course("java");
        course.setId(1L);

        when(courseDao.getById(1L)).thenReturn(Optional.of(course));

        String result = courseService.removeCourse(1L);

        assertEquals("Course 1 removed", result);
    }

    @Test
    void testRemoveCourse_Not_Found() throws IOException {

        Course course = new Course("java");

        when(courseDao.getById(1L)).thenReturn(Optional.of(course));

        String result = courseService.removeCourse(1L);

        assertEquals("Course 1 not found", result);
    }
}