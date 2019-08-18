package apt.project.frontend.controller;

import java.util.List;

import apt.project.backend.domain.BaseEntity;
import apt.project.backend.repository.RepositoryException;

@FunctionalInterface
public interface ThrowingListSupplier<T extends BaseEntity> {
    List<T> get() throws RepositoryException;
}
