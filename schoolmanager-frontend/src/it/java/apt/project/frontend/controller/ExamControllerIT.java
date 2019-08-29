package apt.project.frontend.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;
import apt.project.backend.repository.CourseRepository;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.backend.repository.TransactionManager;
import apt.project.frontend.view.View;

public class ExamControllerIT {

    @Mock
    private View<Exam> examView;

    private ExamController examController;
    private StudentRepository studentRepository;
    private CourseRepository courseRepository;
    private static EntityManagerFactory entityManagerFactory;
    private static TransactionManager<Student> transactionManager;
    private static TransactionManager<Course> courseTransactionManager;

    @BeforeClass
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence
                .createEntityManagerFactory("H2_TEST_FRONTEND");
        transactionManager = new TransactionManager<>(entityManagerFactory);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        entityManagerFactory.close();
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        studentRepository = new StudentRepository(transactionManager);
        transactionManager = new TransactionManager<>(entityManagerFactory);
        courseTransactionManager = new TransactionManager<>(
                entityManagerFactory);
        studentRepository = new StudentRepository(transactionManager);
        courseRepository = new CourseRepository(courseTransactionManager);

        for (Student student : studentRepository.findAll()) {
            studentRepository.delete(student);
        }
        for (Course course : courseRepository.findAll()) {
            courseRepository.delete(course);
        }
        examController = new ExamController(examView, studentRepository);
    }

    @Test
    public void testAllEntities() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 21);
        Student student = new Student("John");
        student.addExam(exam1);
        student.addExam(exam2);

        courseRepository.save(course1);
        courseRepository.save(course2);
        studentRepository.save(student);

        // exercise
        examController.allEntities(student);
        // verify
        verify(examView).showAll(asList(exam1, exam2));
    }

    @Test
    public void testNewEntity() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        String name = "student";
        Student student = new Student(name);
        student.addExam(exam1);

        courseRepository.save(course1);
        courseRepository.save(course2);

        // exercise
        examController.newEntity(student, exam2);

        // verify
        verify(examView).entityAdded(exam2);
    }

    @Test
    public void testDeleteEntity() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam exam1 = new Exam(course1, 22);
        Course course2 = new Course("course2");
        Exam exam2 = new Exam(course2, 22);

        Student student = new Student("student");
        student.addExam(exam1);
        student.addExam(exam2);

        courseRepository.save(course1);
        courseRepository.save(course2);

        // exercise
        examController.deleteEntity(student, exam1);

        // verify
        verify(examView).entityDeleted(exam1);
    }

    @Test
    public void testUpdateEntity() throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Exam originalExam = new Exam(course1, 22);
        Exam modifiedExam = new Exam(course1, 28);

        Student student = new Student("student");
        student.addExam(originalExam);
        courseRepository.save(course1);

        // exercise
        examController.updateEntity(student, modifiedExam);

        // verify
        verify(examView).entityUpdated(modifiedExam);
    }

}
