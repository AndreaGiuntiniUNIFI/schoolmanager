package apt.project.frontend.controller;

import apt.project.backend.domain.Entity;

public interface Controller<T extends Entity> {

    void allEntities();

    void updateEntity(T existingEntity, T modifiedEntity);

    void newEntity(T entity);

    void deleteEntity(T entityToDelete);

}