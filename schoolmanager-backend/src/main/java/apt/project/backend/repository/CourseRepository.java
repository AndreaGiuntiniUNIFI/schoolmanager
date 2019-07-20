package apt.project.backend.repository;

import java.util.List;

import javax.persistence.EntityManager;

import apt.project.backend.domain.Course;

public class CourseRepository implements Repository<Course> {

    private EntityManager entityManager;

    public CourseRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Course> findAll() {
        entityManager.getTransaction().begin();
        List<Course> result = entityManager
                .createQuery("from Course", Course.class).getResultList();
        entityManager.getTransaction().commit();
        return result;
    }

    @Override
    public void save(Course course) {
        entityManager.getTransaction().begin();
        entityManager.persist(course);
        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(Course course) {
        entityManager.getTransaction().begin();
        entityManager.remove(course);
        entityManager.getTransaction().commit();
    }

    @Override
    public void update(Course existingEntity) {
        // TODO Auto-generated method stub

    }

    public Course findByTitle(String titleToFind) {
        entityManager.getTransaction().begin();
        List<Course> result = entityManager
                .createQuery("from Course " + "where title = :title ",
                        Course.class)
                .setParameter("title", titleToFind).setMaxResults(1)
                .getResultList();
        entityManager.getTransaction().commit();

        if (result.size() == 0) {
            return null;
        }

        return result.get(0);
    }

    public void deleteByTitle(String titleToDelete) {
        return;
    }

}
