package apt.project.backend.repository;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;

@RunWith(Parameterized.class)
public class StudentRepositoryIT {

    private EntityManager entityManager;
    private StudentRepository studentRepository;
    private static TransactionManager<Student> transactionManager;

    private static List<EntityManagerFactory> emfList;

    @Parameters
    public static Collection<EntityManagerFactory> data() {
        emfList = asList("POSTGRES", "MYSQL").stream()
                .map(Persistence::createEntityManagerFactory).collect(toList());
        return emfList;
    }

    @Parameter
    public static EntityManagerFactory entityManagerFactory;

    @Before
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM Exam").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM Course").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM Student").executeUpdate();
        entityManager.getTransaction().commit();
        transactionManager = new TransactionManager<>(entityManagerFactory);
        studentRepository = new StudentRepository(transactionManager);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        emfList.stream().filter(Objects::nonNull)
                .forEach(EntityManagerFactory::close);
    }

    @After
    public void tearDown() {
        entityManager.clear();
        entityManager.close();
    }

    @Test
    public void testFindAllWhenDataBaseIsNotEmpty() throws RepositoryException {
        // setUp
        Student student1 = new Student("John");
        Student student2 = new Student("Jane");
        entityManager.getTransaction().begin();
        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.getTransaction().commit();
        // exercise and verify
        assertThat(studentRepository.findAll())
                .containsExactlyInAnyOrder(student1, student2);
    }

    @Test
    public void testFindByNameWhenStudentExists() throws RepositoryException {
        // setUp
        Student student1 = new Student("John");
        Student student2 = new Student("Jane");
        entityManager.getTransaction().begin();
        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.getTransaction().commit();
        // exercise and verify
        assertThat(studentRepository.findByName("John")).isEqualTo(student1);
    }

    @Test
    public void testSave() throws RepositoryException {
        // setup
        Student student = new Student("John");
        // exercise
        studentRepository.save(student);
        // verify
        entityManager.getTransaction().begin();
        List<Student> students = entityManager
                .createQuery("from Student", Student.class).getResultList();
        entityManager.getTransaction().commit();
        assertThat(students).containsExactly(student);
    }

    @Test
    public void testDelete() throws RepositoryException {
        // setup
        Student studentToDelete = new Student("John");
        entityManager.getTransaction().begin();
        entityManager.persist(studentToDelete);
        entityManager.getTransaction().commit();
        // exercise
        studentRepository.delete(studentToDelete);
        // verify
        entityManager.getTransaction().begin();
        List<Student> students = entityManager
                .createQuery("from Student", Student.class).getResultList();
        entityManager.getTransaction().commit();
        assertThat(students).isEmpty();
    }

    @Test
    public void testUpdate() throws RepositoryException {
        // setup
        Student existingStudent = new Student("John");
        entityManager.getTransaction().begin();
        entityManager.persist(existingStudent);
        entityManager.getTransaction().commit();
        entityManager.clear(); // this is done because otherwise Hibernate would
        // update the entity automatically.
        existingStudent.setName("Jane");
        // exercise
        studentRepository.update(existingStudent);
        // verify
        entityManager.getTransaction().begin();
        Student retrievedStudent = entityManager.find(Student.class,
                existingStudent.getId());
        entityManager.getTransaction().commit();

        entityManager.getTransaction().begin();
        assertThat(retrievedStudent).isEqualTo(existingStudent);
        entityManager.getTransaction().commit();
    }

    @Test
    public void testGetAllExamsWhenExamsArePresent()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Course course2 = new Course("course2");
        Student student = new Student("John");
        Exam exam1 = new Exam(course1, 23);
        Exam exam2 = new Exam(course2, 25);
        student.addExam(exam1);
        student.addExam(exam2);

        entityManager.getTransaction().begin();
        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.persist(student);
        entityManager.getTransaction().commit();

        // exercise
        List<Exam> exams = studentRepository.getAllExams(student.getId());

        // verify
        assertThat(exams).containsExactly(exam1, exam2);
    }

}
