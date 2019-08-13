package apt.project.backend.repository;

import static java.util.Arrays.asList;

import java.util.List;

import apt.project.backend.domain.Course;

public class CourseRepository implements Repository<Course> {

    private static final String TABLENAME = "Course";
    private TransactionManager<Course> transactionManager;

    public CourseRepository(TransactionManager<Course> transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public List<Course> findAll() throws RepositoryException {
        return transactionManager.doInTransactionAndReturn(em -> em
                .createQuery("from Course", Course.class).getResultList());
    }

    @Override
    public void save(Course course) throws RepositoryException {
        transactionManager.doInTransaction(em -> em.persist(course));
    }

    @Override
    public void delete(Course course) throws RepositoryException {
        transactionManager.doInTransaction(em -> em.remove(em.merge(course)));
    }

    @Override
    public void update(Course existingCourse) throws RepositoryException {
        transactionManager.doInTransaction(em -> em.merge(existingCourse));
    }

    public Course findByTitle(String titleToFind) throws RepositoryException {
        List<Course> result = transactionManager
                .doInTransactionAndReturn(em -> em
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
        List<Course> result = transactionManager.doInTransactionAndReturn(
                em -> asList(em.find(Course.class, id)));

        return result.get(0);
    }

    @Override
    public String getNameTable() {
        return TABLENAME;
    }

}
