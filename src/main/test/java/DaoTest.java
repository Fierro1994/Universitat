import org.example.Dao.CourseDao;
import org.example.Dao.StudentDao;
import org.example.Dao.TeacherDao;
import org.example.models.Course;
import org.example.models.Student;
import org.example.models.Teacher;
import org.example.service.CreateDBAndDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.sql.*;
import java.util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Testcontainers
public class DaoTest {

    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer().withDatabaseName("dbmelody");

    private static Connection connection;
    private static StudentDao studentDao;
    private static CourseDao courseDao;
    private static TeacherDao teacherDao;
    private static CreateDBAndDTO createDBAndDTO;

    @BeforeAll
    public static void setUp() throws SQLException, ClassNotFoundException {
        MY_SQL_CONTAINER.start();
        connection = DriverManager.getConnection(MY_SQL_CONTAINER.getJdbcUrl(), MY_SQL_CONTAINER.getUsername(), MY_SQL_CONTAINER.getPassword());
        studentDao = new StudentDao(connection);
        courseDao = new CourseDao(connection);
        teacherDao = new TeacherDao(connection);
        createDBAndDTO = new CreateDBAndDTO(connection);
        createDBAndDTO.createDataBase();
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        connection.close();
    }

    @BeforeEach
    public void setupBeforeEach() throws SQLException, ClassNotFoundException {
        createDBAndDTO = new CreateDBAndDTO(connection);
        createDBAndDTO.createDataBase();
    }

    @Test
    public void testGetByIdStudent() throws SQLException {
        Student student = new Student("1@mail.ru", "Виктор", 30);
        studentDao.save(student);
        Optional<Student> result = studentDao.getById(student.getId());
        assertTrue(result.isPresent());
        assertEquals("Виктор", student.getName());
        assertEquals("1@mail.ru", student.getEmail());
        assertEquals(30, student.getAge());
    }
    @Test
    public void testSaveStudent() throws SQLException {
        Student student = new Student("1@mail.ru", "1");
        Student student2 = new Student("2@mail.ru", "2", 1);
        Course course = new Course("java");
        Course course1 = new Course("python");
        Course course2 = new Course("js");
        courseDao.save(course);
        courseDao.save(course1);
        courseDao.save(course2);
        Set<Course> courses = new HashSet<>();
        courses.add(course);
        courses.add(course1);
        courses.add(course2);
        student.setCourses(courses);
        studentDao.save(student);
        studentDao.save(student2);

        Optional<Student> result = studentDao.getById(student.getId());
        Optional<Student> result2 = studentDao.getById(student2.getId());

        assertTrue(result.isPresent());
        assertTrue(result2.isPresent());
        assertEquals(student.getName(), result.get().getName());
        assertEquals(student.getEmail(), result.get().getEmail());
        assertEquals( student.getAge(), result.get().getAge());
        assertEquals(student2.getName(), result2.get().getName());
        assertEquals(student2.getEmail(), result2.get().getEmail());
        assertEquals( student2.getAge(), result2.get().getAge());
        assertEquals(student.getCourses().size(), result.get().getCourses().size());
        assertEquals(0, result2.get().getCourses().size());
         }

    @Test
    public void testUpdateStudent() throws SQLException {
        Student student = new Student("1@mail.ru", "Виктор", 30);
        studentDao.save(student);
        student.setAge(14);
        student.setName("Василий");
        Course course = new Course("java");
        Course course1 = new Course("python");
        Course course2 = new Course("js");
        courseDao.save(course);
        courseDao.save(course1);
        courseDao.save(course2);
        Set<Course> courses = new HashSet<>();
        courses.add(course);
        courses.add(course1);
        courses.add(course2);
        student.setCourses(courses);
        studentDao.update(student);
        Optional<Student> result = studentDao.getById(student.getId());
        assertTrue(result.isPresent());
        assertEquals("Василий", result.get().getName());
        assertEquals(14, result.get().getAge());
        assertEquals(3, result.get().getCourses().size());
    }


    @Test
    public void testRemoveStudent() throws SQLException {
        Student student = new Student("1@mail.ru", "Виктор", 30);
        studentDao.save(student);
        Optional<Student> result = studentDao.getById(student.getId());
        studentDao.remove(student);
        Optional<Student> result2 = studentDao.getById(student.getId());
        assertTrue(result.isPresent());
        assertTrue(!result2.isPresent());

    }

    @Test
    public void testGetByEmailStudents() throws SQLException {
        Student student = new Student("1@mail.ru", "Виктор", 30);
        studentDao.save(student);
        Optional<Student> result = studentDao.getByEmail(student.getEmail());
        assertTrue(result.isPresent());
        assertEquals("Виктор", student.getName());
        assertEquals("1@mail.ru", student.getEmail());
        assertEquals(30, student.getAge());
    }


    @Test
    void testGetAllStudents() throws SQLException {
        Set<Student> expectedStudents = new HashSet<>();
        Student student = new Student("1@mail.ru", "1");
        Student student2 = new Student("2@mail.ru", "2", 1);
        Student student3 = new Student("3@mail.ru", "3", 2);
        studentDao.save(student);
        studentDao.save(student2);
        studentDao.save(student3);
        expectedStudents.add(student);
        expectedStudents.add(student2);
        expectedStudents.add(student3);
        studentDao = new StudentDao(connection);
        Set<Student> sortedActualStudents = new TreeSet<>(Comparator.comparing(Student::getId));
        Set<Student> sortedExpectedStudents = new TreeSet<>(Comparator.comparing(Student::getId));
        Set<Student> actualStudents = studentDao.getAll();
        sortedActualStudents.addAll(actualStudents);
        sortedExpectedStudents.addAll(expectedStudents);
        assertEquals(sortedExpectedStudents, sortedActualStudents);
    }


    ////Courses Test
    @Test
    public void testGetByIdCourse() throws SQLException {
        Course course = new Course("java");
        Student student = new Student("1@mail.ru", "Виктор", 30);
        Student student2 = new Student("2@mail.ru", "Петр", 28);
        studentDao.save(student);
        studentDao.save(student2);
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        teacherDao.save(teacher);
        Set<Student> students = new HashSet<>();
        students.add(student);
        students.add(student2);
        course.setTeacher(teacher);
        course.setStudents(students);
        courseDao.save(course);
        Optional<Course> result = courseDao.getById(course.getId());
        assertTrue(result.isPresent());
        assertEquals("java", course.getName());
        assertEquals(teacher.getName(), result.get().getTeacher().getName());
        assertEquals(students.size(), result.get().getStudents().size());
    }
    @Test
    public void testSaveCourse() throws SQLException {
        Course course = new Course("java");
        Course course1 = new Course("python");
        Course course2 = new Course("js");

        Student student = new Student("1@mail.ru", "Виктор", 30);
        Student student2 = new Student("2@mail.ru", "Петр", 28);
        studentDao.save(student);
        studentDao.save(student2);
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        teacherDao.save(teacher);
        Set<Student> students = new HashSet<>();
        students.add(student);
        students.add(student2);
        course.setTeacher(teacher);
        course.setStudents(students);

        courseDao.save(course);
        courseDao.save(course1);
        courseDao.save(course2);

        Optional<Course> result = courseDao.getById(course.getId());
        Optional<Course> result2 = courseDao.getById(course1.getId());
        Optional<Course> result3 = courseDao.getById(course2.getId());
        assertTrue(result.isPresent());
        assertTrue(result2.isPresent());
        assertTrue(result3.isPresent());
        assertEquals("java", course.getName());
        assertEquals(teacher.getName(), course.getTeacher().getName());
        assertEquals(students.size(), course.getStudents().size());
        assertEquals("python", course1.getName());
        assertEquals("js", course2.getName());
    }



    @Test
    public void testUpdateCourse() throws SQLException {
        Course course = new Course("java");
        courseDao.save(course);
        Optional<Course> result = courseDao.getById(course.getId());
        Student student = new Student("1@mail.ru", "Виктор", 30);
        Student student2 = new Student("2@mail.ru", "Петр", 28);
        studentDao.save(student);
        studentDao.save(student2);
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        teacherDao.save(teacher);
        Set<Student> students = new HashSet<>();
        students.add(student);
        students.add(student2);
        course.setTeacher(teacher);
        course.setStudents(students);
        courseDao.update(course);
        Optional<Course> result2 = courseDao.getById(course.getId());

        assertTrue(result.isPresent());
        assertEquals(0, result.get().getStudents().size());
        assertEquals(2, result2.get().getStudents().size());
    }


    @Test
    public void testRemoveCourse() throws SQLException {
        Course course = new Course("java");
        courseDao.save(course);
        Optional<Course> result = courseDao.getById(course.getId());
        courseDao.remove(course);
        Optional<Course> result2 = courseDao.getById(course.getId());
        assertTrue(result.isPresent());
        assertTrue(!result2.isPresent());

    }

    @Test
    void testGetAllCourses() throws SQLException {
        Set<Course> expectedCourse = new HashSet<>();
        Course course = new Course("java");
        Course course1 = new Course("python");
        Course course2 = new Course("js");
        courseDao.save(course);
        courseDao.save(course1);
        courseDao.save(course2);
        expectedCourse.add(course);
        expectedCourse.add(course1);
        expectedCourse.add(course2);

        Set<Course> sortedActualCourse = new TreeSet<>(Comparator.comparing(Course::getId));
        Set<Course> sortedExpectedCourse = new TreeSet<>(Comparator.comparing(Course::getId));
        Set<Course> actualCourse = courseDao.getAll();
        sortedActualCourse.addAll(actualCourse);
        sortedExpectedCourse.addAll(expectedCourse);
        assertEquals(sortedExpectedCourse, sortedActualCourse);
    }

    ////Teacher Test
    @Test
    public void testGetByIdTeacher() throws SQLException {
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        Course course = new Course("java");
        Course course1 = new Course("python");
        courseDao.save(course);
        courseDao.save(course1);
        Set<Course> courses = new HashSet<>();
        teacher.setCourses(courses);
        teacherDao.save(teacher);
        Optional<Teacher> result = teacherDao.getById(teacher.getId());
        assertTrue(result.isPresent());
        assertEquals(teacher.getName(), result.get().getName());
        assertEquals(teacher.getEmail(), result.get().getEmail());
        assertEquals(teacher.getCourses().size(), result.get().getCourses().size());
    }
    @Test
    public void testSaveTeacher() throws SQLException {
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        Teacher teacher1 = new Teacher("t2@mail.ru", "Павел Алексеевич");
        Teacher teacher2 = new Teacher("t3@mail.ru", "Сергей Владимирович");
        Course course = new Course("java");
        Course course1 = new Course("python");
        courseDao.save(course);
        courseDao.save(course1);
        Set<Course> courses = new HashSet<>();
        teacher.setCourses(courses);
        teacher2.setCourses(courses);
        teacherDao.save(teacher);
        teacherDao.save(teacher1);
        teacherDao.save(teacher2);

        Optional<Teacher> result = teacherDao.getById(teacher.getId());
        Optional<Teacher> result2 = teacherDao.getById(teacher1.getId());
        Optional<Teacher> result3 = teacherDao.getById(teacher2.getId());
        assertTrue(result.isPresent());
        assertTrue(result2.isPresent());
        assertTrue(result3.isPresent());
        assertEquals(teacher.getName(), result.get().getName());
        assertEquals(teacher1.getName(),result2.get().getName());
        assertEquals(teacher2.getName(),result3.get().getName());
        assertEquals(teacher.getCourses().size(), result.get().getCourses().size());

    }



    @Test
    public void testUpdateTeacher() throws SQLException {
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        teacherDao.save(teacher);
        Optional<Teacher> result = teacherDao.getById(teacher.getId());
        Course course = new Course("java");
        Course course1 = new Course("python");
        courseDao.save(course);
        courseDao.save(course1);
        Set<Course> courses = new HashSet<>();
        courses.add(course);
        courses.add(course1);
        teacher.setCourses(courses);
        teacherDao.update(teacher);
        Optional<Teacher> result2 = teacherDao.getById(teacher.getId());
        assertTrue(result.isPresent());
        assertEquals(0, result.get().getCourses().size());
        assertEquals(2, result2.get().getCourses().size());
    }


    @Test
    public void testRemoveTeacher() throws SQLException {
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        teacherDao.save(teacher);
        Optional<Teacher> result = teacherDao.getById(teacher.getId());
        teacherDao.remove(teacher);
        Optional<Teacher> result2 = teacherDao.getById(teacher.getId());
        assertTrue(result.isPresent());
        assertTrue(!result2.isPresent());

    }

    @Test
    void testGetAllTeachers() throws SQLException {
        Set<Teacher> expectedTeacher = new HashSet<>();
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        Teacher teacher1 = new Teacher("t2@mail.ru", "Павел Алексеевич");
        Teacher teacher2 = new Teacher("t3@mail.ru", "Сергей Владимирович");
        teacherDao.save(teacher);
        teacherDao.save(teacher1);
        teacherDao.save(teacher2);
        expectedTeacher.add(teacher);
        expectedTeacher.add(teacher1);
        expectedTeacher.add(teacher2);

        Set<Teacher> sortedActualTeacher = new TreeSet<>(Comparator.comparing(Teacher::getId));
        Set<Teacher> sortedExpectedTeacher = new TreeSet<>(Comparator.comparing(Teacher::getId));
        Set<Teacher> actualTeacher = teacherDao.getAll();
        sortedActualTeacher.addAll(actualTeacher);
        sortedExpectedTeacher.addAll(expectedTeacher);
        assertEquals(sortedExpectedTeacher, sortedActualTeacher);
    }
}