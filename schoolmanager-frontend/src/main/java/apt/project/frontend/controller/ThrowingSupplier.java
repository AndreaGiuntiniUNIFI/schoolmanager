package apt.project.frontend.controller;

import apt.project.backend.domain.BaseEntity;
import apt.project.backend.repository.RepositoryException;

@FunctionalInterface
public interface ThrowingSupplier<T extends BaseEntity> {
    T get() throws RepositoryException;

}
