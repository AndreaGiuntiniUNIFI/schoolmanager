package apt.project.frontend.controller;

import apt.project.backend.repository.RepositoryException;

@FunctionalInterface
public interface ThrowingRunnable {
    void run() throws RepositoryException;

}
