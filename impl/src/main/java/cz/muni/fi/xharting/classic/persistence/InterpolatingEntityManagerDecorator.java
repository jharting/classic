package cz.muni.fi.xharting.classic.persistence;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.jboss.seam.core.Interpolator;

import cz.muni.fi.xharting.classic.util.spi.ForwardingEntityManager;

/**
 * Decorates {@link EntityManager} in order to support EL expressions in JPA queries.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 * 
 */
public class InterpolatingEntityManagerDecorator extends ForwardingEntityManager implements Serializable {

    private static final long serialVersionUID = 1564517413855191126L;
    private EntityManager delegate;
    private Interpolator interpolator;

    public InterpolatingEntityManagerDecorator(EntityManager delegate) {
        this.delegate = delegate;
        this.interpolator = Interpolator.instance();
    }

    @Override
    public EntityManager getEntityManagerDelegate() {
        return delegate;
    }

    @Override
    public Query createQuery(String qlString) {
        return createQuery(qlString, Object.class);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return handleCreateQueryWithString(qlString, resultClass);
    }

    protected <T> TypedQuery<T> handleCreateQueryWithString(String qlString, Class<T> resultClass) {
        if (qlString.indexOf('#') > 0) {
            QueryParser qp = new QueryParser(qlString);
            TypedQuery<T> query = getEntityManagerDelegate().createQuery(qp.getEjbql(), resultClass);
            for (int i = 0; i < qp.getParameterValueExpressions().size(); i++) {
                query.setParameter(QueryParser.getParameterName(i), interpolator.interpolate(qp.getParameterValueExpressions().get(i)));
            }
            return query;
        } else {
            return getEntityManagerDelegate().createQuery(qlString, resultClass);
        }
    }
}
