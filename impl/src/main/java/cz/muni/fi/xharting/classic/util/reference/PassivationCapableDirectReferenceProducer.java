package cz.muni.fi.xharting.classic.util.reference;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;

/**
 * Passivation capable version of {@link DirectReferenceProducer}.
 * 
 * @author Jozef Hartinger
 */
public class PassivationCapableDirectReferenceProducer<T> extends DirectReferenceProducer<T> implements PassivationCapable {

    public PassivationCapableDirectReferenceProducer(DirectReferenceHolder<T> directReferenceHolderProducer, Class<T> clazz, Set<Type> types, Set<Annotation> qualifiers,
            String name, BeanManager manager, boolean checkScope) {
        super(directReferenceHolderProducer, clazz, types, qualifiers, name, manager, checkScope);
    }

    @Override
    public String getId() {
        return PassivationCapableDirectReferenceProducer.class.getName() + "." + getBeanClass();
    }

}
