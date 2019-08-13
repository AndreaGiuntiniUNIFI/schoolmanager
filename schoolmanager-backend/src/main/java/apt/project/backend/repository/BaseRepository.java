package apt.project.backend.repository;

import java.util.List;

import apt.project.backend.domain.BaseEntity;

public class BaseRepository<T extends BaseEntity> implements Repository<T> {

    private TransactionManager<T> transactionManager;

    final Class<T> typeParameterClass;

    private String nameTable;

    public BaseRepository(TransactionManager<T> transactionManager,
            Class<T> typeParameterClass) {
        this.transactionManager = transactionManager;
        this.typeParameterClass = typeParameterClass;
        this.nameTable = typeParameterClass.getName();
    }

    @Override
    public List<T> findAll() throws RepositoryException {
        return transactionManager.doInTransactionAndReturn(
                em -> em.createQuery("from " + nameTable, typeParameterClass)
                        .getResultList());

    }

    @Override
    public void save(T e) throws RepositoryException {
        transactionManager.doInTransaction(em -> em.persist(e));
    }

    @Override
    public void delete(T e) throws RepositoryException {
        transactionManager.doInTransaction(em -> em.remove(em.merge(e)));
    }

    @Override
    public void update(T modifiedEntity) throws RepositoryException {
        // TODO Auto-generated method stub

    }

    @Override
    public T findById(Long id) throws RepositoryException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNameTable() {
        return nameTable;
    }

}
