package apt.project.backend.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import apt.project.backend.domain.Course;

public class TransactionManager {
    private EntityManagerFactory emf;

    public TransactionManager(EntityManagerFactory emf) {
        this.emf = emf;
    }

    List<Course> doInTransaction(TransactionFunction func)
            throws RepositoryException {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();

            List<Course> result = func.execute(em);

            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RepositoryException(e.getMessage());
        } finally {
            em.close();
        }
    }
}