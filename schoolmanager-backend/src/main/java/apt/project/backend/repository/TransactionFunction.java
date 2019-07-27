package apt.project.backend.repository;

import java.util.List;

import javax.persistence.EntityManager;

import apt.project.backend.domain.Course;

@FunctionalInterface
public interface TransactionFunction {
    List<Course> execute(EntityManager em);
}
