package apt.project.frontend.view;

import java.util.List;

import apt.project.backend.domain.BaseEntity;

public interface View<T extends BaseEntity> {

    void showAll(List<T> entity);

    void entityAdded(T entity);

    void showError(String string, T entity);

    void entityDeleted(T entity);

    void entityUpdated(T existingEntity, T modifiedEntity);

}
