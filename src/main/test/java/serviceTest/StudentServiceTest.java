package serviceTest;

import org.example.Dao.StudentDao;
import org.example.Exceptions.EntityNotFoundException;
import org.example.Exceptions.ExistEntityException;
import org.example.dto.StudentDto;
import org.example.models.Student;
import org.example.mappers.StudentMapper;
import org.example.service.StudentsService;
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

public class StudentServiceTest {
    @Mock
    private StudentDao studentDao;

    @InjectMocks
    private StudentsService studentsService;

    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStudent() {
        Student student = new Student("1@mail.ru", "Василий", 30);
        Optional<Student> optionalStudent = Optional.of(student);

        when(studentDao.getById(1L)).thenReturn(optionalStudent);

        Map<Integer, StudentDto> result = studentsService.getStudent(1L);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_FOUND, result.keySet().iterator().next());
    }
    @Test
    void testGetStudent_NotFound() {
        Optional<Student> optionalStudent =Optional.empty();

        when(studentDao.getById(1L)).thenReturn(optionalStudent);

        Map<Integer, StudentDto> result = studentsService.getStudent(1L);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testGetAllStudents() {
        Student student = new Student("1@mail.ru", "Василий", 30);
        Student student1 = new Student("2@mail.ru", "Петр", 22);
        Set<Student> studentDtoList = new HashSet<>();
        studentDtoList.add(student1);
        studentDtoList.add(student);

        when(studentDao.getAll()).thenReturn(studentDtoList);

        Map<Integer, List<StudentDto>> result = studentsService.getAll();

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testGetAllStudents_Not_found() {
        Set<Student> studentDtoList = new HashSet<>();

        when(studentDao.getAll()).thenReturn(studentDtoList);

        Map<Integer, List<StudentDto>> result = studentsService.getAll();

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, result.keySet().iterator().next());
    }

    @Test
    void testAddStudent() throws ExistEntityException, EntityNotFoundException {
        StudentDto studentDto = new StudentDto("1@mail.ru", "Василий", 30);
        Student student = StudentMapper.mapStudent.fromDto(studentDto);

        doNothing().when(studentDao).save(student);

        Map<Integer, StudentDto> result = studentsService.addStudent(studentDto);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_CREATED, result.keySet().iterator().next());
    }

    @Test
    void testUpdateStudent() throws EntityNotFoundException {
        StudentDto studentDto = new StudentDto("1@mail.ru", "Василий", 30);
        Student student = StudentMapper.mapStudent.fromDto(studentDto);

        doNothing().when(studentDao).update(student);

        Map<Integer, StudentDto> result = studentsService.updateStudent(studentDto);

        assertEquals(1, result.size());
        assertEquals(HttpServletResponse.SC_OK, result.keySet().iterator().next());
    }

    @Test
    void testRemoveStudent() throws IOException {

        Student student = new Student("1@mail.ru", "Василий", 30);
        student.setId(1L);

        when(studentDao.getById(1L)).thenReturn(Optional.of(student));

        String result = studentsService.removeStudent(1L);

        assertEquals("Student 1 removed", result);
    }

    @Test
    void testRemoveStudent_Not_Found() throws IOException {

        Student student = new Student("1@mail.ru", "Василий", 30);

        when(studentDao.getById(1L)).thenReturn(Optional.of(student));

        String result = studentsService.removeStudent(1L);

        assertEquals("Student 1 not found", result);
    }
}