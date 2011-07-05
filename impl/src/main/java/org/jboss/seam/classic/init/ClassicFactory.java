package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.seam.classic.init.metadata.FactoryDescriptor;
import org.jboss.seam.solder.literal.NamedLiteral;
import org.jboss.seam.solder.reflection.Reflections;

public class ClassicFactory<T> implements Bean<T> {

    private String name;
    private Class<? extends Annotation> scope;
    private Class<T> beanClass;
    private Set<Type> types = new HashSet<Type>();
    private String hostName;
    private Bean<?> hostBean;
    private Method method;
    private boolean isVoid;
    private BeanManager manager;

    public ClassicFactory(FactoryDescriptor descriptor, Class<T> beanClass, Class<? extends Annotation> scope, String hostName,
            BeanManager manager) {
        this.name = descriptor.getName();
        this.scope = scope;
        this.beanClass = beanClass;
        this.hostName = hostName;
        this.method = descriptor.getMethod();
        this.isVoid = descriptor.isVoid();
        this.manager = manager;
        this.types.add(beanClass);
        this.types.add(Object.class);
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        if (hostBean == null) {
            Set<Bean<?>> beans = manager.getBeans(hostName);
            hostBean = manager.resolve(beans);
            if (hostBean == null) {
                throw new IllegalStateException("Host bean not found.");
            }
        }

        CreationalContext<?> ctx = manager.createCreationalContext(hostBean);
        Object host = Reflections.cast(manager.getReference(hostBean, hostBean.getBeanClass(), ctx));

        T product;

        if (isVoid) {
            // Reflections.invokeMethod(true, method, void.class, host);
            // product = null;
            throw new UnsupportedOperationException();
        } else {
            product = Reflections.invokeMethod(true, method, beanClass, host);
        }

        ctx.release(); // we do not need the host anymore

        return product;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        creationalContext.release();
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.<Annotation> singleton(new NamedLiteral(name));
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [name=" + name + ", scope=" + scope + ", beanClass=" + beanClass
                + ", method=" + method + "]";
    }
}
