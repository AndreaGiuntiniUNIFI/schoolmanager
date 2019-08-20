package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.Course;

public class CourseRepository extends BaseRepository<Course> {

    public CourseRepository(TransactionManager<Course> transactionManager) {
        super(transactionManager, Course.class);
    }

    public Course findByTitle(String titleToFind) throws RepositoryException {
        List<Course> result = transactionManager
                .doInTransactionAndReturn(em -> em
                        .createQuery("from Course " + "where title = :title ",
                                Course.class)
                        .setParameter("title", titleToFind).getResultList());

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

}
