package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.Exam;
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
                        .setParameter("name", name).getResultList());

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public List<Exam> getAllExams(Long id) throws RepositoryException {

        List<Student> result = transactionManager
                .doInTransactionAndReturn(em -> em
                        .createQuery("select stud " + "from Student stud "
                                + "left join fetch stud.exams "
                                + "where stud.id = :id", Student.class)
                        .setParameter("id", id).getResultList());

        if (result.isEmpty()) {
            throw new RepositoryException("The id is not valid: " + id);
        }
        return result.get(0).getExams();
    }

}
