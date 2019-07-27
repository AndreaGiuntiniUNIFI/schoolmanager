package apt.project.backend.repository;

import static java.util.Arrays.asList;

import java.util.List;

import apt.project.backend.domain.Course;

public class CourseRepository implements Repository<Course> {

    private TransactionManager transactionManager;

    public CourseRepository(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public List<Course> findAll() throws RepositoryException {
        return transactionManager.doInTransaction(em -> em
                .createQuery("from Course", Course.class).getResultList());
    }

    @Override
    public void save(Course course) throws RepositoryException {
        transactionManager.doInTransaction(em -> {
            em.persist(course);
            return null;
        });
    }

    @Override
    public void delete(Course course) throws RepositoryException {
        transactionManager.doInTransaction(em -> {
            em.remove(em.merge(course));
            return null;
        });
    }

    @Override
    public void update(Course existingCourse) throws RepositoryException {
        transactionManager.doInTransaction(em -> {
            em.merge(existingCourse);
            return null;
        });
    }

    public Course findByTitle(String titleToFind) throws RepositoryException {
        List<Course> result = transactionManager.doInTransaction(em -> em
                .createQuery("from Course " + "where title = :title ",
                        Course.class)
                .setParameter("title", titleToFind).setMaxResults(1)
                .getResultList());

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    @Override
    public Course findById(Long id) throws RepositoryException {
        List<Course> result = transactionManager
                .doInTransaction(em -> asList(em.find(Course.class, id)));

        return result.get(0);
    }

}
