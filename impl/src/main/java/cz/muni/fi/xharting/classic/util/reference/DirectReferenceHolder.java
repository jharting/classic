package cz.muni.fi.xharting.classic.util.reference;

import java.lang.annotation.Annotation;
import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.UnsatisfiedResolutionException;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.literal.DefaultLiteral;
import org.jboss.solder.reflection.Reflections;

import cz.muni.fi.xharting.classic.util.spi.AbstractBean;

/**
 * Bean implementation for {@link DirectReferenceHolderImpl}, which holds a direct reference to a bean instance. This bean has
 * the scope of the original bean, while the scope of the original bean has been altered to {@link Dependent}.
 * 
 * @author Jozef Hartinger
 * 
 * @param <T> type of reference
 */
public class DirectReferenceHolder<T> extends AbstractBean<DirectReferenceHolderImpl<T>> implements Bean<DirectReferenceHolderImpl<T>> {

    private final BeanManager manager;
    private final Annotation qualifier;
    private Bean<?> bean;

    public DirectReferenceHolder(Class<? extends Annotation> scope, Annotation qualifier, BeanManager manager) {
        super(Reflections.<Class<DirectReferenceHolderImpl<T>>> cast(DirectReferenceHolderImpl.class), scope, Collections.<Annotation> singleton(DefaultLiteral.INSTANCE));
        this.qualifier = qualifier;
        this.manager = manager;
    }

    @Override
    public DirectReferenceHolderImpl<T> create(CreationalContext<DirectReferenceHolderImpl<T>> ctx) {
        T directReference = Reflections.cast(manager.getReference(getTargetBean(), Object.class, ctx));
        return new DirectReferenceHolderImpl<T>(directReference);
    }

    @Override
    public void destroy(DirectReferenceHolderImpl<T> instance, CreationalContext<DirectReferenceHolderImpl<T>> creationalContext) {
        creationalContext.release();
    }

    public Annotation getQualifier() {
        return qualifier;
    }

    private Bean<?> getTargetBean() {
        if (bean == null) {
            bean = manager.resolve(manager.getBeans(Object.class, qualifier));
            if (bean == null) {
                throw new UnsatisfiedResolutionException("Unsatisfied dependency for bean indentified with " + qualifier);
            }
            if (!Dependent.class.equals(bean.getScope())) {
                throw new IllegalStateException("Target bean is not dependent: " + bean);
            }
        }
        return bean;
    }

    @Override
    public String toString() {
        return "DirectReferenceHolder [getTypes()=" + getTypes() + ", getQualifiers()=" + getQualifiers() + "]";
    }
}
