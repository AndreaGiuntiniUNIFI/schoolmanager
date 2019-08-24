package apt.project.frontend.controller;

import java.util.List;

import apt.project.backend.domain.BaseEntity;
import apt.project.backend.repository.Repository;
import apt.project.frontend.view.View;

public class BaseController<T extends BaseEntity> implements Controller<T> {

    protected View<T> view;
    protected Repository<T> repository;
    protected ExceptionManager<T> em;

    public BaseController(View<T> view, Repository<T> repository) {
        this.view = view;
        this.repository = repository;
        this.em = new ExceptionManager<>(view);

    }

    @Override
    public void allEntities() {
        List<T> entities;
        if (!em.listCatcher(() -> repository.findAll())) {
            return;
        }
        entities = em.getResultList();
        view.showAll(entities);
    }

    @Override
    public void updateEntity(T modifiedEntity) {
        T persistedEntity;

        if (!em.catcher(() -> repository.findById(modifiedEntity.getId()),
                modifiedEntity)) {
            return;
        }
        persistedEntity = em.getResult();
        if (persistedEntity == null) {
            view.showError("No existing entity: " + modifiedEntity,
                    modifiedEntity);
            return;
        }
        if (!em.voidCatcher(() -> repository.update(modifiedEntity),
                modifiedEntity)) {
            return;
        }

        view.entityUpdated(modifiedEntity);
    }

    @Override
    public void newEntity(T entity) {
        if (!em.voidCatcher(() -> repository.save(entity), entity)) {
            return;
        }
        view.entityAdded(entity);
    }

    @Override
    public void deleteEntity(T entityToDelete) {
        T persistedEntity;
        if (!em.catcher(() -> repository.findById(entityToDelete.getId()),
                entityToDelete)) {
            return;
        }
        persistedEntity = em.getResult();
        if (persistedEntity == null) {
            view.showError("No existing entity: " + entityToDelete,
                    entityToDelete);
            return;
        }
        if (!em.voidCatcher(() -> repository.delete(entityToDelete),
                entityToDelete)) {
            return;
        }
        view.entityDeleted(entityToDelete);
    }

}
