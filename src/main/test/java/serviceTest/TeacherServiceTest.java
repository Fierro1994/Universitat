package serviceTest;

import org.example.exceptions.EntityNotFoundException;
import org.example.dao.TeacherDao;
import org.example.exceptions.ExistEntityException;
import org.example.dto.TeacherDto;
import org.example.mappers.TeacherMapper;
import org.example.models.Teacher;
import org.example.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TeacherServiceTest {
    @Mock
    private TeacherDao teacherDao;

    @InjectMocks
    private TeacherService teacherService;

    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTeacher() {
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        Optional<Teacher> optionalTeacher = Optional.of(teacher);

        when(teacherDao.getById(1L)).thenReturn(optionalTeacher);

        Map<Integer, TeacherDto> result = teacherService.getTeacher(1L);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testGetTeacher_NotFound() {
        Optional<Teacher> optionalTeacher = Optional.empty();

        when(teacherDao.getById(1L)).thenReturn(optionalTeacher);

        Map<Integer, TeacherDto> result = teacherService.getTeacher(1L);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testGetAllTeachers() {
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        Teacher teacher2 = new Teacher("t2@mail.ru", "Алексей Иванович");
        Set<Teacher> teacherSet = new HashSet<>();
        teacherSet.add(teacher);
        teacherSet.add(teacher2);

        when(teacherDao.getAll()).thenReturn(teacherSet);

        Map<Integer, List<TeacherDto>> result = teacherService.getAll();

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testGetAllTeachers_Not_found() {
        Set<Teacher> teacherSet = new HashSet<>();

        when(teacherDao.getAll()).thenReturn(teacherSet);

        Map<Integer, List<TeacherDto>> result = teacherService.getAll();

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testAddTeacher() throws ExistEntityException, EntityNotFoundException {
        TeacherDto teacherDto = new TeacherDto("t1@mail.ru", "Василий Петрович");
        teacherDto.setId(1L);
        Teacher teacher = TeacherMapper.mapTeacher.fromDto(teacherDto);

        doNothing().when(teacherDao).save(teacher);

        Map<Integer, TeacherDto> result = teacherService.addTeacher(teacherDto);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_CREATED, result.keySet().iterator().next());
    }

    @Test
    void testUpdateTeacher() throws ExistEntityException, EntityNotFoundException {
        TeacherDto teacherDto = new TeacherDto("t1@mail.ru", "Василий Петрович");
        Teacher teacher = TeacherMapper.mapTeacher.fromDto(teacherDto);

        doNothing().when(teacherDao).save(teacher);

        Map<Integer, TeacherDto> result = teacherService.updateTeacher(teacherDto);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_OK, result.keySet().iterator().next());
    }

    @Test
    void testRemoveTeacher() {

        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        teacher.setId(1L);

        when(teacherDao.getById(1L)).thenReturn(Optional.of(teacher));

        String result = teacherService.removeTeacher(1L);

        assertEquals("Teacher 1 removed", result);
    }

    @Test
    void testRemoveTeacher_Not_Found() {

        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");

        when(teacherDao.getById(1L)).thenReturn(Optional.of(teacher));

        String result = teacherService.removeTeacher(1L);

        assertEquals("Teacher 1 not found", result);
    }
}