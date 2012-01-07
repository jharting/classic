package cz.muni.fi.xharting.classic.util.reference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import cz.muni.fi.xharting.classic.util.spi.AbstractBean;

/**
 * Makes the direct reference stored in {@link DirectReferenceHolderImpl} available for injection.
 * 
 * @author Jozef Hartinger
 * 
 * @param <T> type of the entity
 */
public class DirectReferenceProducer<T> extends AbstractBean<T> {

    private final DirectReferenceHolder<T> directReferenceHolder;
    private final BeanManager manager;
    private final boolean checkScope;
    private Bean<?> ip;

    public DirectReferenceProducer(DirectReferenceHolder<T> directReferenceHolder, Class<T> clazz, Set<Type> types, Set<Annotation> qualifiers, String name, BeanManager manager,
            boolean checkScope) {
        super(clazz, types, Dependent.class, name, false, false, qualifiers);
        this.directReferenceHolder = directReferenceHolder;
        this.manager = manager;
        this.checkScope = checkScope;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(CreationalContext<T> creationalContext) {

        checkInjectionPoint(creationalContext);

        DirectReferenceHolderImpl<T> holder = (DirectReferenceHolderImpl<T>) manager.getReference(directReferenceHolder, DirectReferenceHolderImpl.class, creationalContext);
        return holder.getReference();
    }

    protected InjectionPoint getInjectionPoint(CreationalContext<T> creationalContext) {
        if (ip == null) {
            ip = manager.resolve(manager.getBeans(InjectionPoint.class));
        }
        if (ip != null) {
            return (InjectionPoint) manager.getReference(ip, InjectionPoint.class, creationalContext);
        }
        return null;
    }

    protected void checkInjectionPoint(CreationalContext<T> creationalContext) {
        if (checkScope) {
            InjectionPoint ip = getInjectionPoint(creationalContext);
            if (ip != null && ip.getBean() != null) {
                Class<? extends Annotation> scope = ip.getBean().getScope();
                // the injected component must not be of wider scope, other it would be left with a stale reference
                if (ScopeComparator.INSTANCE.compare(scope, directReferenceHolder.getScope()) > 0) {
                    throw new IllegalArgumentException(directReferenceHolder.getScope() + " bean " + this + " cannot be injected into a bean with wider scope " + ip.getBean());
                }
            }
        }
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        creationalContext.release();
    }

    @Override
    public String toString() {
        return "DirectReferenceProducer [getTypes()=" + getTypes() + ", getQualifiers()=" + getQualifiers() + "]";
    }

    private static class ScopeComparator implements Comparator<Class<? extends Annotation>> {

        private static final ScopeComparator INSTANCE = new ScopeComparator();
        private final List<Class<? extends Annotation>> scopes;

        private ScopeComparator() {
            scopes = new ArrayList<Class<? extends Annotation>>();
            scopes.add(RequestScoped.class);
            scopes.add(ConversationScoped.class);
            scopes.add(SessionScoped.class);
            scopes.add(ApplicationScoped.class);
        }

        @Override
        public int compare(Class<? extends Annotation> s1, Class<? extends Annotation> s2) {
            if (!scopes.contains(s1)) {
                throw new IllegalArgumentException(s1 + " is not a built-in normal scope.");
            }
            if (!scopes.contains(s2)) {
                throw new IllegalArgumentException(s2 + " is not a built-in normal scope.");
            }
            return scopes.indexOf(s1) - scopes.indexOf(s2);
        }
    }
}
