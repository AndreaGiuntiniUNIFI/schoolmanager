package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.Entity;

public interface Repository {

    List<Entity> findAll();

    void save(Entity e);

    void delete(Entity e);

    public void update(Entity modifiedEntity);
}