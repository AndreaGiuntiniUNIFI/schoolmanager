package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.Student;

public class StudentRepository implements Repository<Student> {

    private TransactionManager<Student> transactionManager;

    public StudentRepository(TransactionManager<Student> transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public List<Student> findAll() throws RepositoryException {
        return transactionManager.doInTransaction(em -> em
                .createQuery("from Student", Student.class).getResultList());
    }

    @Override
    public void save(Student e) throws RepositoryException {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Student e) throws RepositoryException {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(Student modifiedEntity) throws RepositoryException {
        // TODO Auto-generated method stub

    }

    @Override
    public Student findById(Long id) throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    public Student findByName(String name) throws RepositoryException {
        List<Student> result = transactionManager.doInTransaction(em -> em
                .createQuery("from Student " + "where name = :name ",
                        Student.class)
                .setParameter("name", name).setMaxResults(1).getResultList());

        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

}
