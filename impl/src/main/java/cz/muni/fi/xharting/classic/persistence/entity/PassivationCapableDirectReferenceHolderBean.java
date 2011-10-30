package cz.muni.fi.xharting.classic.persistence.entity;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.PassivationCapable;

/**
 * Passivation capable version of {@link DirectReferenceHolderBean}.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 */
public class PassivationCapableDirectReferenceHolderBean<T> extends DirectReferenceHolderBean<T> implements PassivationCapable {

    public PassivationCapableDirectReferenceHolderBean(Class<T> entityClass, String name, Class<? extends Annotation> scope) {
        super(entityClass, name, scope);
    }

    @Override
    public String getId() {
        return PassivationCapableDirectReferenceHolderBean.class.getName() + getQualifier().name();
    }

}
