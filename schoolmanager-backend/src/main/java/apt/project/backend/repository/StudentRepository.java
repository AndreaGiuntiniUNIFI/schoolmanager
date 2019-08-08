package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.Student;

public class StudentRepository implements Repository<Student> {

    @Override
    public List<Student> findAll() throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
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

    public Student findByName(String name) {
        // TODO Auto-generated method stub
        return null;
    }

}
