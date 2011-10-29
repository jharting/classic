package cz.muni.fi.xharting.classic.metadata;

import org.jboss.seam.annotations.AutoCreate;

public abstract class AbstractManagedInstanceDescriptor {

    private final boolean autoCreate;

    public AbstractManagedInstanceDescriptor(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public AbstractManagedInstanceDescriptor(Class<?> javaClass) {
        this.autoCreate = parseAutoCreate(javaClass);
    }

    public AbstractManagedInstanceDescriptor(Boolean overridenValue, Class<?> javaClass) {
        if (overridenValue != null) {
            autoCreate = overridenValue;
        } else {
            this.autoCreate = parseAutoCreate(javaClass);
        }
    }

    private boolean parseAutoCreate(Class<?> javaClass) {
        return javaClass.isAnnotationPresent(AutoCreate.class)
                || (javaClass.getPackage() != null && javaClass.getPackage().isAnnotationPresent(AutoCreate.class));
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }
}
