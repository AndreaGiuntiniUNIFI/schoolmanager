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
    public void updateEntity(T modifiedEntity) {
        if (ExceptionManager.catcher(
                () -> repository.findById(modifiedEntity.getId()), view,
                modifiedEntity) == null) {
            view.showError("No existing entity: " + modifiedEntity,
                    modifiedEntity);
            return;
        }
        ExceptionManager.voidCatcher(() -> repository.update(modifiedEntity),
                view, modifiedEntity);
        view.entityUpdated(modifiedEntity);
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
