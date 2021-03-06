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

@RunWith(Parameterized.class)
public class CourseRepositoryIT {

    private EntityManager entityManager;
    private CourseRepository courseRepository;
    private static TransactionManager<Course> transactionManager;

    private static List<EntityManagerFactory> emfList;

    @Parameters
    public static Collection<EntityManagerFactory> data() {
        emfList = asList("POSTGRES_TEST_BACKEND", "MYSQL_TEST_BACKEND").stream()
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
        courseRepository = new CourseRepository(transactionManager);
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
        Course course1 = new Course("Course1");
        Course course2 = new Course("Course2");
        entityManager.getTransaction().begin();
        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.getTransaction().commit();
        // exercise and verify
        assertThat(courseRepository.findAll()).containsExactly(course1,
                course2);
    }

    @Test
    public void testFindByTitleWhenCourseExist() throws RepositoryException {
        // setUp
        Course course1 = new Course("Course1");
        Course course2 = new Course("Course2");
        entityManager.getTransaction().begin();
        entityManager.persist(course1);
        entityManager.persist(course2);
        entityManager.getTransaction().commit();
        // exercise and verify
        assertThat(courseRepository.findByTitle("Course1")).isEqualTo(course1);
    }

    @Test
    public void testSave() throws RepositoryException {
        // setup
        Course course = new Course("Course1");
        // exercise
        courseRepository.save(course);
        // verify
        entityManager.getTransaction().begin();
        List<Course> courses = entityManager
                .createQuery("from Course", Course.class).getResultList();
        entityManager.getTransaction().commit();
        assertThat(courses).containsExactly(course);
    }

    @Test
    public void testDelete() throws RepositoryException {
        // setup
        Course courseToDelete = new Course("Course1");
        entityManager.getTransaction().begin();
        entityManager.persist(courseToDelete);
        entityManager.getTransaction().commit();
        // exercise
        courseRepository.delete(courseToDelete);
        // verify
        entityManager.getTransaction().begin();
        List<Course> courses = entityManager
                .createQuery("from Course", Course.class).getResultList();
        entityManager.getTransaction().commit();
        assertThat(courses).isEmpty();
    }

    @Test
    public void testUpdate() throws RepositoryException {
        // setup
        Course existingCourse = new Course("Course1");
        entityManager.getTransaction().begin();
        entityManager.persist(existingCourse);
        entityManager.getTransaction().commit();
        entityManager.clear(); // this is done because otherwise Hibernate would
                               // update the entity automatically.
        existingCourse.setTitle("Course1Modified");
        // exercise
        courseRepository.update(existingCourse);
        // verify
        entityManager.getTransaction().begin();
        Course retrievedCourse = entityManager.find(Course.class,
                existingCourse.getId());
        entityManager.getTransaction().commit();

        assertThat(retrievedCourse)
                .isEqualToComparingFieldByField(existingCourse);
    }

}
