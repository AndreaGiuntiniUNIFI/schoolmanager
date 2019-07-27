package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.BaseEntity;

public interface Repository<T extends BaseEntity> {

    List<T> findAll() throws RepositoryException;

    void save(T e) throws RepositoryException;

    void delete(T e) throws RepositoryException;

    public void update(T modifiedEntity) throws RepositoryException;

    public T findById(Long id) throws RepositoryException;
}