package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.BaseEntity;

public interface Repository<T extends BaseEntity> {

    List<T> findAll();

    void save(T e);

    void delete(T e);

    public void update(T modifiedEntity);
}