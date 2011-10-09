package org.jboss.seam.classic.init.metadata;

import java.lang.reflect.Method;

public class ObserverMethodDescriptor extends AbstractObserverMethodDescriptor {

    private final ManagedBeanDescriptor bean;
    private final Method method;
    private final boolean autoCreate;

    public ObserverMethodDescriptor(String type, ManagedBeanDescriptor bean, Method method, boolean autoCreate) {
        super(type);
        this.bean = bean;
        this.method = method;
        this.autoCreate = autoCreate;
    }

    public ManagedBeanDescriptor getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }
}
