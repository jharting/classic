package cz.muni.fi.xharting.classic.metadata;

import java.lang.reflect.Method;

import org.jboss.solder.reflection.Reflections;

public class ObserverMethodDescriptor extends AbstractObserverMethodDescriptor {

    private final ManagedBeanDescriptor bean;
    private final Method method;
    private final boolean autoCreate;

    public ObserverMethodDescriptor(String type, ManagedBeanDescriptor bean, Method method, boolean autoCreate) {
        super(type);
        this.bean = bean;
        this.method = method;
        this.autoCreate = autoCreate;
        Reflections.setAccessible(method);
    }
    
    public ObserverMethodDescriptor(ObserverMethodDescriptor original, ManagedBeanDescriptor bean)
    {
        this(original.getType(), bean, original.getMethod(), original.isAutoCreate());
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
