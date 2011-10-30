package cz.muni.fi.xharting.classic.persistence.entity;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.PassivationCapable;

/**
 * Passivation capable version of {@link EntityProducer}.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 */
public class PassivationCapableEntityProducer<T> extends EntityProducer<T> implements PassivationCapable {

    public PassivationCapableEntityProducer(DirectReferenceHolderBean<T> directReferenceHolderProducer, BeanManager manager,
            Class<T> entityClass, String name) {
        super(directReferenceHolderProducer, manager, entityClass, name);
    }

    @Override
    public String getId() {
        return PassivationCapableEntityProducer.class.getName() + "." + getName();
    }

}
