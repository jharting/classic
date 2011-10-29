package cz.muni.fi.xharting.classic.factory;

import java.lang.reflect.Method;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.reflection.Reflections;

import cz.muni.fi.xharting.classic.metadata.FactoryDescriptor;
import cz.muni.fi.xharting.classic.util.CdiUtils;

public class LegacyFactory extends AbstractLegacyFactory<Object> {

    private Class<?> beanClass;
    private String hostName;
    private Class<?> hostType;
    private Method method;
    private boolean isVoid;

    public LegacyFactory(FactoryDescriptor descriptor, BeanManager manager) {
        super(descriptor.getName(), descriptor.getCdiScope(), manager);
        this.beanClass = descriptor.getMethod().getReturnType();
        this.hostName = descriptor.getBean().getImplicitRole().getName();
        this.hostType = descriptor.getBean().getJavaClass();
        this.method = descriptor.getMethod();
        this.isVoid = void.class.equals(this.beanClass);
        if (isVoid) {
            addTypes(Object.class, Void.class);
        } else {
            addTypes(Object.class, this.beanClass);
        }
    }

    @Override
    public Object create(CreationalContext<Object> creationalContext) {

        CdiUtils.ManagedBeanInstance<?> host = CdiUtils.lookupBeanByName(hostName, hostType, getManager());

        try {
            if (isVoid)
            {
                Reflections.invokeMethod(false, method, beanClass, host.getInstance());
                /*
                 * this value must be overriden by an outjected value
                 * otherwise, an attempt to inject it results in an exception
                 */
                return Void.INSTANCE;
            }
            else
            {
                return Reflections.invokeMethod(true, method, beanClass, host.getInstance());
            }
        } finally {
            host.getCreationalContext().release();
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }
}
