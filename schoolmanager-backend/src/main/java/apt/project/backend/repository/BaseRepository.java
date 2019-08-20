package apt.project.backend.repository;

import static java.util.Arrays.asList;

import java.util.List;

import apt.project.backend.domain.BaseEntity;

public class BaseRepository<T extends BaseEntity> implements Repository<T> {

    protected TransactionManager<T> transactionManager;

    protected final Class<T> typeParameterClass;

    private String nameTable;

    public BaseRepository(TransactionManager<T> transactionManager,
            Class<T> typeParameterClass) {
        this.transactionManager = transactionManager;
        this.typeParameterClass = typeParameterClass;
        this.setNameTable(typeParameterClass.getName());
    }

    @Override
    public List<T> findAll() throws RepositoryException {
        StringBuilder queryBuilder = new StringBuilder("from ")
                .append(getNameTable());
        return transactionManager.doInTransactionAndReturn(em -> em
                .createQuery(queryBuilder.toString(), typeParameterClass)
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
        transactionManager.doInTransaction(em -> em.merge(modifiedEntity));
    }

    @Override
    public T findById(Long id) throws RepositoryException {
        List<T> result = transactionManager.doInTransactionAndReturn(
                em -> asList(em.find(typeParameterClass, id)));
        return result.get(0);
    }

    public String getNameTable() {
        return nameTable;
    }

    public void setNameTable(String nameTable) {
        this.nameTable = nameTable;
    }

}
