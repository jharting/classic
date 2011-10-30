package cz.muni.fi.xharting.classic.persistence.entity;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import cz.muni.fi.xharting.classic.util.Annotations;
import cz.muni.fi.xharting.classic.util.spi.AbstractBean;

/**
 * Produces an non-wrapped reference to an entity. This is done by storing the entity in the {@link DirectReferenceHolder}.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 * @param <T> type of the entity
 */
public class EntityProducer<T> extends AbstractBean<T> {

    private final DirectReferenceHolderBean<T> directReferenceHolderProducer;
    private final BeanManager manager;

    public EntityProducer(DirectReferenceHolderBean<T> directReferenceHolderProducer, BeanManager manager,
            Class<T> entityClass, String name) {
        super(entityClass, entityClass, Dependent.class, name, false, false, Annotations.getQualifiers(entityClass, manager));
        this.directReferenceHolderProducer = directReferenceHolderProducer;
        this.manager = manager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(CreationalContext<T> creationalContext) {

        DirectReferenceHolder<T> holder = (DirectReferenceHolder<T>) manager.getReference(directReferenceHolderProducer,
                DirectReferenceHolder.class, creationalContext);
        return holder.getReference();
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        creationalContext.release();
    }
}
