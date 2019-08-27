package apt.project.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import apt.project.backend.domain.Course;
import apt.project.backend.domain.Exam;
import apt.project.backend.domain.Student;

public class StudentRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private StudentRepository studentRepository;
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
    public void setUp() {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM Exam").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM Course").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM Student").executeUpdate();
        entityManager.getTransaction().commit();
        studentRepository = new StudentRepository(transactionManager);

    }

    @After
    public void tearDown() {
        entityManager.clear();
        entityManager.close();
    }

    @Test
    public void testFindAllWhenDataBaseIsEmpty() throws RepositoryException {
        // exercise and verify
        assertThat(studentRepository.findAll()).isEmpty();
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
        assertThat(studentRepository.findAll()).containsExactly(student1,
                student2);
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
    public void testFindByNameWhenStudentDoesNotExist()
            throws RepositoryException {
        // exercise and verify
        assertThat(studentRepository.findByName("John")).isNull();
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
        // assertThat(retrievedStudent).isEqualTo(existingStudent);
        entityManager.getTransaction().commit();
    }

    @Test
    public void testFindById() throws RepositoryException {
        // setup
        Student student = new Student("John");
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();
        // exercise
        Student retrievedStudent = studentRepository.findById(student.getId());
        // verify
        assertThat(student).isEqualTo(retrievedStudent);
    }

    @Test
    public void testWhenStudentIsDeletedThenAllHisExamsAreDeleted()
            throws RepositoryException {
        // setup
        Student student = new Student("John");
        Student student2 = new Student("Jane");
        Course course = new Course("course");
        Exam exam = new Exam(course, 30);
        Exam exam2 = new Exam(course, 23);
        student.addExam(exam);
        student2.addExam(exam2);
        entityManager.getTransaction().begin();
        entityManager.persist(course);
        entityManager.persist(student2);
        entityManager.persist(student);
        entityManager.getTransaction().commit();

        // exercise
        studentRepository.delete(student);

        // verify
        entityManager.getTransaction().begin();
        List<Exam> exams = entityManager.createQuery("from Exam", Exam.class)
                .getResultList();
        entityManager.getTransaction().commit();
        assertThat(exams).containsExactly(exam2);
    }

    @Test
    public void testGetAllExamsWhenThereAreNoExams()
            throws RepositoryException {
        // setup
        Student student = new Student("John");
        entityManager.getTransaction().begin();
        entityManager.persist(student);
        entityManager.getTransaction().commit();

        // exercise
        List<Exam> exams = studentRepository.getAllExams(student.getId());

        // verify
        assertThat(exams).isEmpty();
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

    @Test
    public void testGetAllExamsWhenStudentIdDoesNotExist()
            throws RepositoryException {
        // setup
        Course course1 = new Course("course1");
        Student student = new Student("John");
        Exam exam1 = new Exam(course1, 23);
        student.addExam(exam1);

        entityManager.getTransaction().begin();
        entityManager.persist(course1);
        entityManager.persist(student);
        entityManager.getTransaction().commit();

        // exercise and verify
        assertThatThrownBy(() -> studentRepository.getAllExams(-1L))
                .isInstanceOf(RepositoryException.class)
                .hasMessage("Repository exception: The id is not valid: -1");
    }

}
