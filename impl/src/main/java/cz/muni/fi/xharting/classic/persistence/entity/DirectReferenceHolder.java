package cz.muni.fi.xharting.classic.persistence.entity;

import java.io.Serializable;

import org.jboss.solder.core.Veto;

/**
 * Simple reference carrier. It is registered as bean using {@link DirectReferenceHolderBean}.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 * @param <T> type of reference
 */
@Veto
public class DirectReferenceHolder<T> implements Serializable {

    private static final long serialVersionUID = 8143843055252632981L;

    private final T reference;

    public DirectReferenceHolder() {
        this.reference = null;
    }

    public DirectReferenceHolder(T reference) {
        this.reference = reference;
    }

    public T getReference() {
        return reference;
    }
}
