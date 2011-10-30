package cz.muni.fi.xharting.classic.test.persistence.entity;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("injectedBean")
public class InjectedBean {

    @In(create = true)
    private SerializableEntity entity;
    @In(create = true)
    private SerializableEntity entity2;
    @In(create = true)
    private NonSerializableEntity entity3;

    public SerializableEntity getEntity() {
        return entity;
    }

    public SerializableEntity getEntity2() {
        return entity2;
    }

    public NonSerializableEntity getEntity3() {
        return entity3;
    }

}
