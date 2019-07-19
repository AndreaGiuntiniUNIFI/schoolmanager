package apt.project.frontend.controller;

import apt.project.backend.domain.BaseEntity;

public interface Controller<T extends BaseEntity> {

    void allEntities();

    void updateEntity(T existingEntity, T modifiedEntity);

    void newEntity(T entity);

    void deleteEntity(T entityToDelete);

}