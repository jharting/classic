package cz.muni.fi.xharting.classic.util.reference;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.reflection.Reflections;
import org.jboss.solder.reflection.Synthetic;

public class DirectReferenceFactory {

    public static final String NAMESPACE = "cz.muni.fi.xharting.classic.util.reference";
    public static final Synthetic.Provider syntheticProvider = new Synthetic.Provider(NAMESPACE);

    private DirectReferenceFactory() {
    }

    /**
     * Creates a pair of {@link DirectReferenceHolder} and {@link DirectReferenceProducer} based on the parameters, which
     * represent a bean.
     * 
     * @param type the java type of the bean
     * @param types java types of the bean
     * @param qualifiers qualifiers of the bean
     * @param name the name of the bean
     * @param qualifier identifying the original suppressed bean. The caller is responsible for suppressing and identifying the
     *        original bean (can be achieved using synthetic qualifiers) and also for changing its scope to {@link Dependent}.
     * @param scope the scope of the bean
     * @param manager
     * @param checkScope indicates whether the scope check (see {@link DirectReference} javadoc) should be performed. The check
     *        is performed at run time.
     * @return a list containing {@link DirectReferenceHolder} and {@link DirectReferenceProducer}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<Bean<T>> createDirectReferenceHolder(Class<T> type, Set<Type> types, Set<Annotation> qualifiers, String name, Annotation qualifier,
            Class<? extends Annotation> scope, BeanManager manager, boolean checkScope) {
        boolean serializable = type.isPrimitive() || Serializable.class.isAssignableFrom(type);
        DirectReferenceHolder<T> holder;
        DirectReferenceProducer<T> producer;

        if (serializable) {
            holder = new PassivationCapableDirectReferenceHolder<T>(scope, qualifier, manager);
            producer = new PassivationCapableDirectReferenceProducer<T>(holder, type, types, qualifiers, name, manager, checkScope);
        } else {
            holder = new DirectReferenceHolder<T>(scope, qualifier, manager);
            producer = new DirectReferenceProducer<T>(holder, type, types, qualifiers, name, manager, checkScope);
        }
        return Reflections.cast(Arrays.asList(holder, producer));
    }
}
