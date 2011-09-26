package org.jboss.seam.classic.init.metadata;

import org.jboss.seam.annotations.AutoCreate;

public abstract class AbstractManagedInstanceDescriptor {

    private boolean autoCreate;

    public AbstractManagedInstanceDescriptor(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public AbstractManagedInstanceDescriptor(Class<?> javaClass) {
        this.autoCreate = javaClass.isAnnotationPresent(AutoCreate.class)
                || (javaClass.getPackage() != null && javaClass.getPackage().isAnnotationPresent(AutoCreate.class));
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }
}
