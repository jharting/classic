package org.jboss.seam.classic.runtime;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;
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

import org.jboss.seam.classic.init.SeamClassicExtension;
import org.jboss.seam.classic.init.metadata.BeanDescriptor;
import org.jboss.seam.classic.init.metadata.InjectionPointDescriptor;
import org.jboss.seam.solder.reflection.Reflections;

@Interceptor
@BijectionInterceptor.Bijected
public class BijectionInterceptor implements Serializable {

    private static final long serialVersionUID = -2311065357544463370L;

    @Inject
    private SeamClassicExtension extension;
    @Inject
    private BeanManager manager;
    private Set<InjectionPointDescriptor> injectionPoints;

    @PostConstruct
    public void postConstruct(InvocationContext ctx) {
        initInjectionPointDescriptors(ctx);

        inject(ctx.getTarget());

        // proceed initializer chain
        try {
            ctx.proceed();
        } catch (Exception e) {
            // TODO: throw something
        }

        // TODO: outject
        // TODO: disinject
    }

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ctx) throws Exception {
        if (injectionPoints == null) {
            initInjectionPointDescriptors(ctx);
        }

        inject(ctx.getTarget());

        return ctx.proceed();
    }

    protected void initInjectionPointDescriptors(InvocationContext ctx) {

        Class<?> targetClass = ctx.getTarget().getClass();
        Collection<BeanDescriptor> descriptors = extension.getDescriptorsByClass(targetClass);
        if (descriptors.isEmpty()) {
            // let's try again with superclass since the original class might have been a subclass which CDI implementation
            // uses to implement interceptors and decorators
            descriptors = extension.getDescriptorsByClass(targetClass.getSuperclass());
            if (descriptors.isEmpty()) {
                throw new IllegalStateException(targetClass + " is not a Seam bean.");
            }
        }

        // since all the bean descriptors share the same class, any is OK for us
        BeanDescriptor descriptor = descriptors.iterator().next();
        injectionPoints = descriptor.getInjectionPoints();
    }

    protected void inject(Object target) {
        for (InjectionPointDescriptor injectionPoint : injectionPoints) {
            BeanDescriptor candidate = extension.getDescriptor(injectionPoint.getName());

            Set<Bean<?>> beans = manager.getBeans(injectionPoint.getName());
            Bean<?> bean = manager.resolve(beans);

            Object object = null;

            // TODO: check OutjectedValueHolder

            if (injectionPoint.isCreate() || (candidate != null && candidate.isAutoCreate())) {
                if (bean == null) {
                    // TODO: ?
                    // throw exception
                } else {
                    CreationalContext<?> ctx = manager.createCreationalContext(bean);
                    object = manager.getReference(bean, injectionPoint.getField().getType(), ctx);
                }
            } else {
                if (bean != null) {
                    Context context = manager.getContext(bean.getScope());
                    object = context.get(bean);
                }
            }

            if (object != null) {
                Reflections.setFieldValue(true, injectionPoint.getField(), target, object);
            } else if (injectionPoint.isRequired()) {
                // TODO: throw exception
            }
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
