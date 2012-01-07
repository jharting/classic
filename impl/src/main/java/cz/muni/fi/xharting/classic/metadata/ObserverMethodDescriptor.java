package cz.muni.fi.xharting.classic.metadata;

import java.lang.reflect.Method;

import org.jboss.solder.reflection.Reflections;

/**
 * Represents a legacy observer method defined on a Seam component.
 * 
 * @author Jozef Hartinger
 * 
 */
public class ObserverMethodDescriptor extends AbstractObserverMethodDescriptor {

    private final BeanDescriptor bean;
    private final Method method;
    private final boolean autoCreate;

    public ObserverMethodDescriptor(String type, BeanDescriptor bean, Method method, boolean autoCreate) {
        super(type);
        this.bean = bean;
        this.method = method;
        this.autoCreate = autoCreate;
        Reflections.setAccessible(method);
    }

    public ObserverMethodDescriptor(ObserverMethodDescriptor original, BeanDescriptor bean) {
        this(original.getType(), bean, original.getMethod(), original.isAutoCreate());
    }

    public BeanDescriptor getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }
}
