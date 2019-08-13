package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.Student;

public class StudentRepository extends BaseRepository<Student> {

    public StudentRepository(TransactionManager<Student> transactionManager) {
        super(transactionManager, Student.class);
    }

    public Student findByName(String name) throws RepositoryException {
        List<Student> result = transactionManager
                .doInTransactionAndReturn(em -> em
                        .createQuery("from Student " + "where name = :name ",
                                Student.class)
                        .setParameter("name", name).setMaxResults(1)
                        .getResultList());

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

}
