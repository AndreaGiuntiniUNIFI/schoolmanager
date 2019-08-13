package apt.project.backend.repository;

import static java.util.Arrays.asList;

import java.util.List;

import apt.project.backend.domain.Student;

public class StudentRepository implements Repository<Student> {

    private static final String TABLENAME = "Student";
    private TransactionManager<Student> transactionManager;

    public StudentRepository(TransactionManager<Student> transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public List<Student> findAll() throws RepositoryException {
        return transactionManager.doInTransactionAndReturn(em -> em
                .createQuery("from Student", Student.class).getResultList());
    }

    @Override
    public void save(Student e) throws RepositoryException {
        transactionManager.doInTransaction(em -> em.persist(e));
    }

    @Override
    public void delete(Student e) throws RepositoryException {
        transactionManager.doInTransaction(em -> em.remove(em.merge(e)));
    }

    @Override
    public void update(Student modifiedEntity) throws RepositoryException {
        transactionManager.doInTransaction(em -> em.merge(modifiedEntity));
    }

    @Override
    public Student findById(Long id) throws RepositoryException {
        List<Student> result = transactionManager.doInTransactionAndReturn(
                em -> asList(em.find(Student.class, id)));
        return result.get(0);
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

    @Override
    public String getNameTable() {
        return TABLENAME;
    }

}
