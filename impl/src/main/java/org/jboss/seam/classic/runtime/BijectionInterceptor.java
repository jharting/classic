package org.jboss.seam.classic.runtime;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InterceptorBinding;
import javax.interceptor.InvocationContext;

import org.jboss.seam.InstantiationException;
import org.jboss.seam.RequiredException;
import org.jboss.seam.classic.init.metadata.AbstractManagedInstanceDescriptor;
import org.jboss.seam.classic.init.metadata.InjectionPointDescriptor;
import org.jboss.seam.classic.init.metadata.ManagedBeanDescriptor;
import org.jboss.seam.classic.init.metadata.MetadataRegistry;
import org.jboss.seam.classic.init.metadata.OutjectionPointDescriptor;
import org.jboss.seam.classic.runtime.outjection.RewritableContextManager;
import org.jboss.seam.solder.reflection.Reflections;

@Interceptor
@BijectionInterceptor.Bijected
public class BijectionInterceptor implements Serializable {

    private static final long serialVersionUID = -2311065357544463370L;

    @Inject
    private MetadataRegistry registry;
    @Inject
    private BeanManager manager;
    @Inject
    private RewritableContextManager rewritableContextManager;

    private ManagedBeanDescriptor descriptor;

    public BijectionInterceptor() {

    }

    protected void init(InvocationContext ctx) {
        Class<?> targetClass = ctx.getTarget().getClass();
        Collection<ManagedBeanDescriptor> descriptors = registry.getManagedInstanceDescriptorByClass(targetClass, true);
        // since all the bean descriptors share the same class, any is OK for us
        descriptor = descriptors.iterator().next();
    }

    @PostConstruct
    public void postConstruct(InvocationContext ctx) {
        init(ctx);

        Object target = ctx.getTarget();
        inject(target);

        // proceed initializer chain
        try {
            ctx.proceed();
        } catch (Exception e) {
            throw new InstantiationException("Could not instantiate Seam component: " + descriptor.getJavaClass(), e);
        }

        outject(target);
        disinject(target);
    }

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ctx) throws Exception {
        if (descriptor == null) {
            init(ctx);
        }

        Object target = ctx.getTarget();

        inject(target);

        Object result = ctx.proceed();

        outject(target);
        disinject(target);

        return result;
    }

    protected void inject(Object target) {
        for (InjectionPointDescriptor injectionPoint : descriptor.getInjectionPoints()) {
            Object injectableReference = getInjectableReference(injectionPoint);

            if (injectableReference != null) {
                Reflections.setFieldValue(true, injectionPoint.getField(), target, injectableReference);
            } else if (injectionPoint.isRequired()) {
                throw new RequiredException("@In attribute requires non-null value: " + injectionPoint.getPath());
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Object getInjectableReference(InjectionPointDescriptor injectionPoint) {
        return getInjectableReference(injectionPoint, RequestScoped.class, ConversationScoped.class, SessionScoped.class,
                ApplicationScoped.class);
    }

    protected Object getInjectableReference(InjectionPointDescriptor injectionPoint, Class<? extends Annotation>... scopes) {
        String injectionPointName = injectionPoint.getName();
        AbstractManagedInstanceDescriptor candidate = registry.getManagedInstanceDescriptorByName(injectionPointName);

        Set<Bean<?>> beans = manager.getBeans(injectionPointName);
        Bean<?> bean = manager.resolve(beans);

        boolean create = injectionPoint.isCreate() || (candidate != null && candidate.isAutoCreate());

        for (Class<? extends Annotation> scope : scopes) {
            if (!isContextActive(scope)) {
                continue;
            }

            Object reference = rewritableContextManager.get(injectionPointName, scope);
            if (reference != null) {
                return reference;
            }

            if (bean != null && scope.equals(bean.getScope())) {
                if (create) {
                    CreationalContext<?> ctx = manager.createCreationalContext(bean);
                    return manager.getReference(bean, injectionPoint.getField().getType(), ctx);
                } else {
                    Context context = manager.getContext(bean.getScope());
                    Object value = context.get(bean);
                    if (value != null) {
                        return value;
                    }
                }
            }
        }
        return null;
    }

    // TODO is there a better way to find out?
    protected boolean isContextActive(Class<? extends Annotation> scope) {
        try {
            manager.getContext(scope);
            return true;
        } catch (ContextNotActiveException e) {
            return false;
        }
    }

    protected void outject(Object target) {
        for (OutjectionPointDescriptor outjectionPoint : descriptor.getOutjectionPoints()) {
            outjectField(outjectionPoint, target);
        }
    }

    protected void outjectField(OutjectionPointDescriptor outjectionPoint, Object target) {
        Field field = outjectionPoint.getField();
        if (!field.isAccessible()) {
            Reflections.setAccessible(field);
        }
        Object value = Reflections.getFieldValue(outjectionPoint.getField(), target);

        if (value == null && outjectionPoint.isRequired()) {
            throw new RequiredException("@Out attribute requires non-null value: " + outjectionPoint.getPath());
        }

        rewritableContextManager.set(outjectionPoint.getName(), value, outjectionPoint.getCdiScope());
    }

    protected void disinject(Object target) {
        for (InjectionPointDescriptor injectionPoint : descriptor.getInjectionPoints()) {
            Reflections.setFieldValue(true, injectionPoint.getField(), target, null);
        }
    }

    @InterceptorBinding
    @Target({ TYPE })
    @Retention(RUNTIME)
    @Documented
    public static @interface Bijected {

        @SuppressWarnings("all")
        public static class BijectedLiteral extends AnnotationLiteral<Bijected> implements Bijected {
            private static final long serialVersionUID = -5531820054700986956L;
            public static final BijectedLiteral INSTANCE = new BijectedLiteral();
        }
    }
}
