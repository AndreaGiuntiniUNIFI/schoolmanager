package apt.project.frontend.view;

import java.util.List;

import apt.project.backend.domain.BaseEntity;

public interface View<T extends BaseEntity> {

    void showAll(List<T> entities);

    void entityAdded(T entity);

    void entityDeleted(T entity);

    void showError(String string, T entity);

    void entityUpdated(T modifiedEntity);

}
