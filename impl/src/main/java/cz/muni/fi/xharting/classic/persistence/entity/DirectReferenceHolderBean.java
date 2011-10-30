package cz.muni.fi.xharting.classic.persistence.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;
import javax.persistence.EntityManager;

/**
 * This bean allows for non-injected, unintercepted but scoped components that can be passed to {@link EntityManager} (are not
 * proxied nor subclassed).
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 * @param <T> type of reference
 */
public class DirectReferenceHolderBean<T> implements Bean<DirectReferenceHolder<T>> {

    private final Class<T> entityClass;
    private final Constructor<T> entityClassConstructor;
    private final Class<? extends Annotation> scope;
    private final DirectReferenceHolderQualifierLiteral qualifier;
    private final Set<Type> types = new HashSet<Type>();

    public DirectReferenceHolderBean(Class<T> entityClass, String name, Class<? extends Annotation> scope) {
        this.entityClass = entityClass;
        this.qualifier = new DirectReferenceHolderQualifierLiteral(name);
        this.scope = scope;
        types.add(Object.class);
        types.add(DirectReferenceHolder.class);

        // prepare constructor
        try {
            this.entityClassConstructor = entityClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(entityClass + " does not have a no-args constructor", e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(entityClass + " does not have a non-private no-args constructor", e);
        }
        if (!entityClassConstructor.isAccessible()) {
            entityClassConstructor.setAccessible(true);
        }
    }

    @Override
    public DirectReferenceHolder<T> create(CreationalContext<DirectReferenceHolder<T>> creationalContext) {
        T entity = null;
        try {
            entity = entityClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Exception instantiating new instance of " + entityClass, e);
        }
        return new DirectReferenceHolder<T>(entity);
    }

    @Override
    public void destroy(DirectReferenceHolder<T> instance, CreationalContext<DirectReferenceHolder<T>> creationalContext) {
        creationalContext.release();
    }

    @Override
    public Set<Type> getTypes() {
        return Collections.unmodifiableSet(types);
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.<Annotation> singleton(qualifier);
    }

    public DirectReferenceHolderQualifierLiteral getQualifier() {
        return qualifier;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Class<?> getBeanClass() {
        return DirectReferenceHolder.class;
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Qualifier
    @Target({ TYPE, METHOD, PARAMETER, FIELD })
    @Retention(RUNTIME)
    static @interface DirectReferenceHolderQualifier {

        String name();
    }

    @SuppressWarnings("all")
    static class DirectReferenceHolderQualifierLiteral extends AnnotationLiteral<DirectReferenceHolderQualifier> implements
            DirectReferenceHolderQualifier {
        private String name;

        public DirectReferenceHolderQualifierLiteral(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
