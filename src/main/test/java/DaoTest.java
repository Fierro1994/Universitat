import org.example.Dao.CourseDao;
import org.example.Dao.StudentDao;
import org.example.Dao.TeacherDao;
import org.example.models.Course;
import org.example.models.Student;
import org.example.models.Teacher;
import org.example.service.CreateDBAndDTO;
import org.junit.jupiter.api.*;
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
    public static void setUp() throws SQLException {
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
    public void setupBeforeEach() {
        createDBAndDTO = new CreateDBAndDTO(connection);
        createDBAndDTO.createDataBase();
    }

    @Test
    public void testGetByIdStudent()  {
        Student student = new Student("1@mail.ru", "Виктор", 30);
        studentDao.save(student);
        Optional<Student> result = studentDao.getById(student.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Виктор", student.getName());
        Assertions.assertEquals("1@mail.ru", student.getEmail());
        Assertions.assertEquals(30, student.getAge());
    }
    @Test
    public void testSaveStudent()  {
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

        Assertions.assertTrue(result.isPresent());
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertEquals(student.getName(), result.get().getName());
        Assertions.assertEquals(student.getEmail(), result.get().getEmail());
        Assertions.assertEquals(student.getAge(), result.get().getAge());
        Assertions.assertEquals(student2.getName(), result2.get().getName());
        Assertions.assertEquals(student2.getEmail(), result2.get().getEmail());
        Assertions.assertEquals(student2.getAge(), result2.get().getAge());
        Assertions.assertEquals(student.getCourses().size(), result.get().getCourses().size());
        Assertions.assertEquals(0, result2.get().getCourses().size());
         }

    @Test
    public void testUpdateStudent() {
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
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Василий", result.get().getName());
        Assertions.assertEquals(14, result.get().getAge());
        Assertions.assertEquals(3, result.get().getCourses().size());
    }


    @Test
    public void testRemoveStudent(){
        Student student = new Student("1@mail.ru", "Виктор", 30);
        studentDao.save(student);
        Optional<Student> result = studentDao.getById(student.getId());
        studentDao.remove(student);
        Optional<Student> result2 = studentDao.getById(student.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertFalse(result2.isPresent());

    }

    @Test
    public void testGetByEmailStudents() {
        Student student = new Student("1@mail.ru", "Виктор", 30);
        studentDao.save(student);
        Optional<Student> result = studentDao.getByEmail(student.getEmail());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Виктор", student.getName());
        Assertions.assertEquals("1@mail.ru", student.getEmail());
        Assertions.assertEquals(30, student.getAge());
    }


    @Test
    void testGetAllStudents(){
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
        Assertions.assertEquals(sortedExpectedStudents, sortedActualStudents);
    }


    ////Courses Test
    @Test
    public void testGetByIdCourse() {
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
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("java", course.getName());
        Assertions.assertEquals(teacher.getName(), result.get().getTeacher().getName());
        Assertions.assertEquals(students.size(), result.get().getStudents().size());
    }
    @Test
    public void testSaveCourse()  {
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
        students.add(student);
        course.setTeacher(teacher);
        course.setStudents(students);

        courseDao.save(course);
        courseDao.save(course1);
        courseDao.save(course2);

        Optional<Course> result = courseDao.getById(course.getId());
        Optional<Course> result2 = courseDao.getById(course1.getId());
        Optional<Course> result3 = courseDao.getById(course2.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertTrue(result3.isPresent());
        Assertions.assertEquals("java", course.getName());
        Assertions.assertEquals(teacher.getName(), course.getTeacher().getName());
        Assertions.assertEquals(students.size(), course.getStudents().size());
        Assertions.assertEquals("python", course1.getName());
        Assertions.assertEquals("js", course2.getName());
    }



    @Test
    public void testUpdateCourse() {
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
        students.add(student);
        course.setStudents(students);
        courseDao.update(course);

        Optional<Course> result2 = courseDao.getById(course.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(0, result.get().getStudents().size());
        Assertions.assertEquals(2, result2.get().getStudents().size());
    }


    @Test
    public void testRemoveCourse()  {
        Course course = new Course("java");
        courseDao.save(course);
        Optional<Course> result = courseDao.getById(course.getId());
        courseDao.remove(course);
        Optional<Course> result2 = courseDao.getById(course.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertFalse(result2.isPresent());

    }

    @Test
    void testGetAllCourses() {
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
        Assertions.assertEquals(sortedExpectedCourse, sortedActualCourse);
    }

    ////Teacher Test
    @Test
    public void testGetByIdTeacher()  {
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        Course course = new Course("java");
        Course course1 = new Course("python");
        courseDao.save(course);
        courseDao.save(course1);
        Set<Course> courses = new HashSet<>();
        teacher.setCourses(courses);
        teacherDao.save(teacher);
        Optional<Teacher> result = teacherDao.getById(teacher.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(teacher.getName(), result.get().getName());
        Assertions.assertEquals(teacher.getEmail(), result.get().getEmail());
        Assertions.assertEquals(teacher.getCourses().size(), result.get().getCourses().size());
    }
    @Test
    public void testSaveTeacher()  {
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        Teacher teacher1 = new Teacher("t2@mail.ru", "Павел Алексеевич");
        Teacher teacher2 = new Teacher("t3@mail.ru", "Сергей Владимирович");
        Course course = new Course("java");
        Course course1 = new Course("python");
        courseDao.save(course);
        courseDao.save(course1);
        Set<Course> courses = new HashSet<>();
        courses.add(course);
        courses.add(course1);
        teacher.setCourses(courses);
        teacherDao.save(teacher);
        teacherDao.save(teacher1);
        teacherDao.save(teacher2);

        Optional<Teacher> result = teacherDao.getById(teacher.getId());
        Optional<Teacher> result2 = teacherDao.getById(teacher1.getId());
        Optional<Teacher> result3 = teacherDao.getById(teacher2.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertTrue(result2.isPresent());
        Assertions.assertTrue(result3.isPresent());
        Assertions.assertEquals(teacher.getName(), result.get().getName());
        Assertions.assertEquals(teacher1.getName(), result2.get().getName());
        Assertions.assertEquals(teacher2.getName(), result3.get().getName());
        Assertions.assertEquals(teacher.getCourses().size(), result.get().getCourses().size());

    }



    @Test
    public void testUpdateTeacher(){
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
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(0, result.get().getCourses().size());
        Assertions.assertEquals(2, result2.get().getCourses().size());
    }


    @Test
    public void testRemoveTeacher(){
        Teacher teacher = new Teacher("t1@mail.ru", "Василий Петрович");
        teacherDao.save(teacher);
        Optional<Teacher> result = teacherDao.getById(teacher.getId());
        teacherDao.remove(teacher);
        Optional<Teacher> result2 = teacherDao.getById(teacher.getId());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertFalse(result2.isPresent());

    }

    @Test
    void testGetAllTeachers() {
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
        Assertions.assertEquals(sortedExpectedTeacher, sortedActualTeacher);
    }
}