package apt.project.frontend.controller;

import apt.project.backend.domain.Course;
import apt.project.backend.repository.RepositoryException;
import apt.project.frontend.view.View;

public final class ExceptionManager {

    private ExceptionManager() {
    }

    static Course catcher(ThrowingSupplier supplier, View<Course> view,
            Course course) {
        try {
            return supplier.get();
        } catch (RepositoryException e) {
            view.showError(e.getMessage(), course);
        }
        return null;
    }

    static Course catcher(ThrowingSupplier supplier, View<Course> view) {
        return catcher(supplier, view, null);
    }
}
