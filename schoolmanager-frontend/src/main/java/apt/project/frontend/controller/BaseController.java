package apt.project.frontend.controller;

import apt.project.backend.domain.BaseEntity;
import apt.project.backend.repository.Repository;
import apt.project.frontend.view.View;

public class BaseController<T extends BaseEntity> implements Controller<T> {

    protected View<T> view;
    protected Repository<T> repository;

    public BaseController(View<T> view, Repository<T> repository) {
        this.view = view;
        this.repository = repository;
    }

    @Override
    public void allEntities() {
        ExceptionManager.voidCatcher(() -> view.showAll(repository.findAll()),
                view);
    }

    @Override
    public void updateEntity(T existingEntity, T modifiedEntity) {
        if (ExceptionManager.catcher(
                () -> repository.findById(existingEntity.getId()), view,
                existingEntity) == null) {
            view.showError("No existing entity: " + existingEntity,
                    existingEntity);
            return;
        }
        ExceptionManager.voidCatcher(() -> repository.update(modifiedEntity),
                view, existingEntity);
        view.entityUpdated(existingEntity, modifiedEntity);
    }

    @Override
    public void newEntity(T entity) {
        ExceptionManager.voidCatcher(() -> repository.save(entity), view,
                entity);
        view.entityAdded(entity);
    }

    @Override
    public void deleteEntity(T entityToDelete) {
        if (ExceptionManager.catcher(
                () -> repository.findById(entityToDelete.getId()), view,
                entityToDelete) == null) {
            view.showError("No existing entity: " + entityToDelete,
                    entityToDelete);
            return;
        }
        ExceptionManager.voidCatcher(() -> repository.delete(entityToDelete),
                view, entityToDelete);
        view.entityDeleted(entityToDelete);
    }

}
