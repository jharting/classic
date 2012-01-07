package cz.muni.fi.xharting.classic.util.reference;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;

/**
 * Passivation capable version of {@link DirectReferenceHolder}.
 * 
 * @author Jozef Hartinger
 */
public class PassivationCapableDirectReferenceHolder<T> extends DirectReferenceHolder<T> implements PassivationCapable {

    public PassivationCapableDirectReferenceHolder(Class<? extends Annotation> scope, Annotation qualifier, BeanManager manager) {
        super(scope, qualifier, manager);
    }

    @Override
    public String getId() {
        return PassivationCapableDirectReferenceHolder.class.getName() + getBeanClass();
    }

}
