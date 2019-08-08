package apt.project.backend.repository;

import java.util.List;

import javax.persistence.EntityManager;

import apt.project.backend.domain.BaseEntity;

@FunctionalInterface
public interface TransactionFunction<T extends BaseEntity> {
    List<T> execute(EntityManager em);
}
