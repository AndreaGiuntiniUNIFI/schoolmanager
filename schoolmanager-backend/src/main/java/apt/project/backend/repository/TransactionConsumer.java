package apt.project.backend.repository;

import javax.persistence.EntityManager;

@FunctionalInterface
public interface TransactionConsumer {
    void accept(EntityManager em);
}
