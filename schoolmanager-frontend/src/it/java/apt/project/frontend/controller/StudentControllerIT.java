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

import apt.project.backend.domain.Student;
import apt.project.backend.repository.RepositoryException;
import apt.project.backend.repository.StudentRepository;
import apt.project.backend.repository.TransactionManager;
import apt.project.frontend.view.View;

public class StudentControllerIT {

    @Mock
    private View<Student> studentView;

    private StudentController studentController;
    private StudentRepository studentRepository;
    private static EntityManagerFactory entityManagerFactory;
    private static TransactionManager<Student> transactionManager;

    @BeforeClass
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("H2");
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
        studentRepository = new StudentRepository(transactionManager);
        for (Student student : studentRepository.findAll()) {
            studentRepository.delete(student);
        }
        studentController = new StudentController(studentView,
                studentRepository);
    }

    @Test
    public void testAllEntities() throws RepositoryException {
        // setup
        Student student = new Student("John");
        studentRepository.save(student);
        // exercise
        studentController.allEntities();
        // verify
        verify(studentView).showAll(asList(student));
    }

    @Test
    public void testNewEntity() throws RepositoryException {
        // setup
        Student student = new Student("John");
        // exercise
        studentController.newEntity(student);
        // verify
        verify(studentView).entityAdded(student);
    }

    @Test
    public void testDeleteEntity() throws RepositoryException {
        // setup
        Student student = new Student("John");
        studentRepository.save(student);
        // exercise
        studentController.deleteEntity(student);
        // verify
        verify(studentView).entityDeleted(student);
    }

    @Test
    public void testUpdateEntity() throws RepositoryException {
        // setup
        Student student = new Student("John");
        studentRepository.save(student);
        // exercise
        student.setName("Jane");
        studentController.updateEntity(student);
        // verify
        verify(studentView).entityUpdated(student);
    }

}
