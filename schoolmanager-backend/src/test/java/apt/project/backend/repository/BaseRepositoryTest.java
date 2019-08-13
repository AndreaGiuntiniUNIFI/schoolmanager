package apt.project.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BaseRepositoryTest {

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    private BaseRepository<TestEntity> repository;
    private static TransactionManager<TestEntity> transactionManager;

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
        entityManager.createNativeQuery("TRUNCATE TABLE public.TestEntity")
                .executeUpdate();
        entityManager.getTransaction().commit();
        repository = new BaseRepository<TestEntity>(transactionManager,
                TestEntity.class);
    }

    @After
    public void tearDown() {
        entityManager.clear();
        entityManager.close();
    }

    @Test
    public void testFindAllWhenDataBaseIsEmpty() throws RepositoryException {
        // exercise and verify
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    public void testFindAllWhenDataBaseIsNotEmpty() throws RepositoryException {
        // setUp
        TestEntity testEntity1 = new TestEntity();
        TestEntity testEntity2 = new TestEntity();
        entityManager.getTransaction().begin();
        entityManager.persist(testEntity1);
        entityManager.persist(testEntity2);
        entityManager.getTransaction().commit();
        // exercise and verify
        assertThat(repository.findAll()).containsExactly(testEntity1,
                testEntity2);
    }

    @Test
    public void testSave() throws RepositoryException {
        // setup
        TestEntity entity = new TestEntity();
        // exercise
        repository.save(entity);
        // verify
        entityManager.getTransaction().begin();
        List<TestEntity> entities = entityManager
                .createQuery("from TestEntity", TestEntity.class)
                .getResultList();
        entityManager.getTransaction().commit();
        assertThat(entities).containsExactly(entity);
    }

    @Test
    public void testDelete() throws RepositoryException {
        // setup
        TestEntity entityToDelete = new TestEntity();
        entityManager.getTransaction().begin();
        entityManager.persist(entityToDelete);
        entityManager.getTransaction().commit();
        // exercise
        repository.delete(entityToDelete);
        // verify
        entityManager.getTransaction().begin();
        List<TestEntity> entities = entityManager
                .createQuery("from TestEntity", TestEntity.class)
                .getResultList();
        entityManager.getTransaction().commit();
        assertThat(entities).isEmpty();
    }

    @Test
    public void testUpdate() throws RepositoryException {
        // setup
        TestEntity existingEntity = new TestEntity();
        existingEntity.setaField("originalField");
        entityManager.getTransaction().begin();
        entityManager.persist(existingEntity);
        entityManager.getTransaction().commit();
        entityManager.clear(); // this is done because otherwise Hibernate would
                               // update the entity automatically.
        existingEntity.setaField("modifiedField");
        // exercise
        repository.update(existingEntity);
        // verify
        entityManager.getTransaction().begin();
        TestEntity retrievedCourse = entityManager.find(TestEntity.class,
                existingEntity.getId());
        entityManager.getTransaction().commit();

        assertThat(retrievedCourse)
                .isEqualToComparingFieldByField(existingEntity);
    }

    @Test
    public void testFindById() throws RepositoryException {
        // setup
        TestEntity entity = new TestEntity();
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
        // exercise
        TestEntity retrievedEntity = repository.findById(entity.getId());
        // verify
        assertThat(entity).isEqualTo(retrievedEntity);
    }
}
