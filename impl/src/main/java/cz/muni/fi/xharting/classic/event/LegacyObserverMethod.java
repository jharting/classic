package cz.muni.fi.xharting.classic.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.reflection.Reflections;

import cz.muni.fi.xharting.classic.metadata.ObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.util.CdiUtils;

/**
 * Represents a Seam 2 observer method.
 * 
 * @author Jozef Hartinger
 * 
 */
public class LegacyObserverMethod extends AbstractLegacyObserverMethod {

    private String hostName;
    private Class<?> hostType;
    private Method method;

    public LegacyObserverMethod(String hostName, ObserverMethodDescriptor descriptor, TransactionPhase transactionPhase, BeanManager manager) {
        super(descriptor.getType(), transactionPhase, descriptor.isAutoCreate(), manager);
        this.hostName = hostName;
        this.hostType = descriptor.getBean().getJavaClass();
        this.method = descriptor.getMethod();
        if (method.isAccessible()) {
            Reflections.setAccessible(method);
        }
    }

    public void notify(EventPayload event) {
        CdiUtils.ManagedBeanInstance<?> hostInstance = CdiUtils.lookupBeanByName(hostName, hostType, getManager());
        try {
            method.invoke(hostInstance.getInstance(), event.getParameters());
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException("exception invoking: " + method.getName(), cause);
            }
        } catch (Throwable e) {
            throw new RuntimeException("exception invoking: " + method.getName(), e);
        } finally {
            hostInstance.release();
        }
    }

    @Override
    public Class<?> getBeanClass() {
        return hostType;
    }

    @Override
    public String toString() {
        return "LegacyObserverMethod [method=" + method + "]";
    }
}
