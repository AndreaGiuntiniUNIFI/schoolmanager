package apt.project.frontend.controller;

import apt.project.backend.domain.BaseEntity;
import apt.project.backend.repository.Repository;
import apt.project.frontend.view.View;

public class BaseController<T extends BaseEntity> implements Controller<T> {

    private View<T> view;
    private Repository<T> repository;

    @Override
    public void allEntities() {
        ExceptionManager.catcher(() -> {
            view.showAll(repository.findAll());
            return null;
        }, view);
    }

    @Override
    public void updateEntity(T existingEntity, T modifiedEntity) {
        // TODO Auto-generated method stub

    }

    @Override
    public void newEntity(T entity) {
        ExceptionManager.catcher(() -> {
            repository.save(entity);
            return null;
        }, view, entity);
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
        ExceptionManager.catcher(() -> {
            repository.delete(entityToDelete);
            return null;
        }, view, entityToDelete);
        view.entityDeleted(entityToDelete);
    }

}
