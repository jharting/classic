package org.jboss.seam.classic.init.factory;

import java.lang.reflect.Method;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.seam.classic.init.metadata.FactoryDescriptor;
import org.jboss.seam.classic.util.CdiUtils;
import org.jboss.seam.solder.reflection.Reflections;

public class LegacyFactory<PRODUCT_TYPE, HOST_TYPE> extends AbstractLegacyFactory<PRODUCT_TYPE> {

    private Class<PRODUCT_TYPE> beanClass;
    private String hostName;
    private Class<HOST_TYPE> hostType;
    private Method method;

    public LegacyFactory(FactoryDescriptor descriptor, Class<PRODUCT_TYPE> beanClass, Class<HOST_TYPE> hostType, BeanManager manager) {
        super(descriptor.getName(), descriptor.getCdiScope(), manager);
        this.beanClass = beanClass;
        
        this.hostName = descriptor.getBean().getImplicitRole().getName();
        this.hostType = hostType;
        this.method = descriptor.getMethod();
        addTypes(Object.class, beanClass);
    }

    @Override
    public PRODUCT_TYPE create(CreationalContext<PRODUCT_TYPE> creationalContext) {
        
        CdiUtils.ManagedBeanInstance<HOST_TYPE> host = CdiUtils.lookupBeanByName(hostName, hostType, getManager());

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
    public Class<?> getBeanClass() {
        return beanClass;
    }
}
