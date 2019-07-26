package apt.project.frontend.controller;

import apt.project.backend.domain.Course;
import apt.project.backend.repository.RepositoryException;

@FunctionalInterface
public interface ThrowingSupplier {
    Course get() throws RepositoryException;

}