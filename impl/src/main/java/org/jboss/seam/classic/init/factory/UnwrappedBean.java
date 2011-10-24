package org.jboss.seam.classic.init.factory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.classic.scope.StatelessScoped;
import org.jboss.seam.classic.util.CdiUtils;
import org.jboss.solder.reflection.Reflections;


public class UnwrappedBean extends AbstractLegacyFactory<Object> {

    private Method method;
    // no need to store the name of the host, since it is the same as the name of the unwrapped bean itself
    private Class<?> type;
    private Class<?> hostType;

    public UnwrappedBean(String name, Class<?> hostType, Type genericBeanType, Method method, BeanManager manager) {
        super(name, StatelessScoped.class, manager);
        this.method = method;
        this.hostType = hostType;
        this.type = method.getReturnType();
        addTypes(Object.class, genericBeanType);
    }

    @Override
    public Object create(CreationalContext<Object> creationalContext) {

        CdiUtils.ManagedBeanInstance<?> host = CdiUtils.lookupBeanByInternalName(getName(), hostType, getManager());

        try {
            return Reflections.invokeMethod(true, method, type, host.getInstance());
        } finally {
            host.getCreationalContext().release();
        }
    }
}
