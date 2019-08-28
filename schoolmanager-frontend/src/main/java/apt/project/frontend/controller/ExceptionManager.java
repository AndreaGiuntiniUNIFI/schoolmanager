package apt.project.frontend.controller;

import static java.util.Arrays.asList;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apt.project.backend.domain.BaseEntity;
import apt.project.backend.repository.RepositoryException;
import apt.project.frontend.view.View;

public class ExceptionManager<T extends BaseEntity> {

    private static final Logger LOGGER = LogManager
            .getLogger(ExceptionManager.class);

    private List<T> resultList;
    private View<T> view;

    ExceptionManager(View<T> view) {
        this.view = view;
    }

    public boolean listCatcher(ThrowingListSupplier<T> supplier, T entity) {
        try {
            clearResultList();
            resultList = supplier.get();
            return true;
        } catch (RepositoryException e) {
            view.showError(e.getMessage() + ", " + entity);
            LOGGER.error("", e);
        }
        return false;
    }

    public boolean listCatcher(ThrowingListSupplier<T> supplier) {
        return listCatcher(supplier, null);
    }

    public boolean catcher(ThrowingSupplier<T> supplier, T entity) {
        return listCatcher(() -> asList(supplier.get()), entity);
    }

    public boolean catcher(ThrowingSupplier<T> supplier) {
        return catcher(supplier, null);
    }

    public boolean voidCatcher(ThrowingRunnable runnable, T entity) {
        return catcher(() -> {
            runnable.run();
            return null;
        }, entity);
    }

    public boolean voidCatcher(ThrowingRunnable runnable) {
        return voidCatcher(runnable::run, null);
    }

    public T getResult() {
        return resultList.get(0);
    }

    public List<T> getResultList() {
        return resultList;
    }

    public void clearResultList() {
        resultList = null;
    }

}
