package apt.project.frontend.controller;

import apt.project.backend.domain.BaseEntity;
import apt.project.backend.repository.RepositoryException;
import apt.project.frontend.view.View;

public final class ExceptionManager {

    private ExceptionManager() {
    }

    static <T extends BaseEntity> T catcher(ThrowingSupplier<T> supplier,
            View<T> view, T entity) {
        try {
            return supplier.get();
        } catch (RepositoryException e) {
            view.showError(e.getMessage(), entity);
        }
        return null;
    }

    static <T extends BaseEntity> T catcher(ThrowingSupplier<T> supplier,
            View<T> view) {
        return catcher(supplier, view, null);
    }
}
