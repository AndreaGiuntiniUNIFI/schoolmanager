package apt.project.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        entityManager.createNativeQuery("TRUNCATE TABLE public.Student")
                .executeUpdate();
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

}
