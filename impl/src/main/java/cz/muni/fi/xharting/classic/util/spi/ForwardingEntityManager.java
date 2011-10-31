package cz.muni.fi.xharting.classic.util.spi;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

/**
 * Utility implementation of {@link EntityManager} which delegates all method calls to the underlying delegate instance.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public abstract class ForwardingEntityManager implements EntityManager {

    public abstract EntityManager getEntityManagerDelegate();

    @Override
    public void persist(Object entity) {
        getEntityManagerDelegate().persist(entity);
    }

    @Override
    public <T> T merge(T entity) {
        return getEntityManagerDelegate().merge(entity);
    }

    @Override
    public void remove(Object entity) {
        getEntityManagerDelegate().remove(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return getEntityManagerDelegate().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return getEntityManagerDelegate().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return getEntityManagerDelegate().find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return getEntityManagerDelegate().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return getEntityManagerDelegate().getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        getEntityManagerDelegate().flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        getEntityManagerDelegate().setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return getEntityManagerDelegate().getFlushMode();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        getEntityManagerDelegate().lock(entity, lockMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        getEntityManagerDelegate().lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity) {
        getEntityManagerDelegate().refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        getEntityManagerDelegate().refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        getEntityManagerDelegate().refresh(entity, lockMode);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        getEntityManagerDelegate().refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        getEntityManagerDelegate().clear();
    }

    @Override
    public void detach(Object entity) {
        getEntityManagerDelegate().detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        return getEntityManagerDelegate().contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        return getEntityManagerDelegate().getLockMode(entity);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        getEntityManagerDelegate().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return getEntityManagerDelegate().getProperties();
    }

    @Override
    public Query createQuery(String qlString) {
        return getEntityManagerDelegate().createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return getEntityManagerDelegate().createQuery(criteriaQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return getEntityManagerDelegate().createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(String name) {
        return getEntityManagerDelegate().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return getEntityManagerDelegate().createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return getEntityManagerDelegate().createNativeQuery(sqlString);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        return getEntityManagerDelegate().createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return getEntityManagerDelegate().createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public void joinTransaction() {
        getEntityManagerDelegate().joinTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return getEntityManagerDelegate().unwrap(cls);
    }

    @Override
    public void close() {
        getEntityManagerDelegate().close();
    }

    @Override
    public boolean isOpen() {
        return getEntityManagerDelegate().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return getEntityManagerDelegate().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return getEntityManagerDelegate().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return getEntityManagerDelegate().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return getEntityManagerDelegate().getMetamodel();
    }

    @Override
    public Object getDelegate() {
        return getEntityManagerDelegate().getDelegate();
    }
}
