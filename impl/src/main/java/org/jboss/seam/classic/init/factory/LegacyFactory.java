package org.jboss.seam.classic.init.factory;

import java.lang.reflect.Method;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.classic.init.metadata.FactoryDescriptor;
import org.jboss.seam.classic.util.CdiUtils;
import org.jboss.seam.solder.reflection.Reflections;

public class LegacyFactory<T> extends AbstractLegacyFactory<T> {

    private Class<T> beanClass;
    private String hostName;
    private Method method;
    private BeanManager manager;

    public LegacyFactory(FactoryDescriptor descriptor, Class<T> beanClass, BeanManager manager) {
        super(descriptor.getName(), descriptor.getCdiScope(), manager);
        this.beanClass = beanClass;
        
        this.hostName = descriptor.getBean().getImplicitRole().getName();
        this.method = descriptor.getMethod();
        this.manager = manager;
        addTypes(Object.class, beanClass);
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        
        CdiUtils.ManagedBeanInstance<T> host = CdiUtils.lookupBean(hostName, beanClass, manager);

        try
        {
            return Reflections.invokeMethod(true, method, beanClass, host.getInstance());
        }
        finally
        {
            host.getCreationalContext().release();
        }
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        creationalContext.release();
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }
}
