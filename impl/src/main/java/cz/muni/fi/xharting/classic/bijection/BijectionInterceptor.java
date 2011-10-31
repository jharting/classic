package cz.muni.fi.xharting.classic.bijection;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.List;
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
import javax.persistence.EntityManager;

import org.jboss.seam.InstantiationException;
import org.jboss.seam.RequiredException;

import cz.muni.fi.xharting.classic.metadata.AbstractManagedInstanceDescriptor;
import cz.muni.fi.xharting.classic.metadata.DecoratingInjectionPoint;
import cz.muni.fi.xharting.classic.metadata.InjectionPointDescriptor;
import cz.muni.fi.xharting.classic.metadata.BeanDescriptor;
import cz.muni.fi.xharting.classic.metadata.MetadataRegistry;
import cz.muni.fi.xharting.classic.metadata.OutjectionPointDescriptor;
import cz.muni.fi.xharting.classic.persistence.InterpolatingEntityManagerDecorator;
import cz.muni.fi.xharting.classic.persistence.entity.EntityProducer;
import cz.muni.fi.xharting.classic.scope.ScopeExtension;
import cz.muni.fi.xharting.classic.util.CdiUtils;

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
    @Inject
    private ScopeExtension extension;

    private BeanDescriptor descriptor;

    public BijectionInterceptor() {

    }

    protected void init(InvocationContext ctx) {
        Class<?> targetClass = ctx.getTarget().getClass();
        Collection<BeanDescriptor> descriptors = registry.getManagedInstanceDescriptorByClass(targetClass, true);
        // since all the bean descriptors share the same class, any is OK for us
        descriptor = descriptors.iterator().next();
    }

    @PostConstruct
    public void postConstruct(InvocationContext ctx) {
        init(ctx);

        Object target = ctx.getTarget();
        decorateEEInjectedFields(target);
        inject(target, false);

        // proceed initializer chain
        try {
            ctx.proceed();
        } catch (Exception e) {
            throw new InstantiationException("Could not instantiate Seam component: " + descriptor.getJavaClass(), e);
        }

        outject(target, false);
        disinject(target);
    }

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ctx) throws Exception {
        if (descriptor == null) {
            init(ctx);
        }

        Object target = ctx.getTarget();

        inject(target, true);

        Object result = ctx.proceed();

        outject(target, true);
        disinject(target);

        return result;
    }

    protected void inject(Object target, boolean enforce) {
        for (InjectionPointDescriptor injectionPoint : descriptor.getInjectionPoints()) {
            Object injectableReference = getInjectableReference(injectionPoint);

            if (injectableReference != null) {
                if (injectableReference instanceof Void) {
                    throw new IllegalStateException("Factory method did not outject a value. Unable to injected reference: "
                            + injectionPoint.getName());
                }
                injectionPoint.set(target, injectableReference);
            } else if (enforce && injectionPoint.isRequired()) {
                throw new RequiredException("@In attribute requires non-null value: " + injectionPoint.getPath());
            }
        }
    }

    protected void decorateEEInjectedFields(Object target) {
        for (DecoratingInjectionPoint<EntityManager> field : descriptor.getPersistenceContextFields()) {
            EntityManager delegate = field.get(target);
            if (delegate == null) {
                throw new IllegalStateException("EE-injected field " + field.getField() + " is null.");
            }
            EntityManager decorator = new InterpolatingEntityManagerDecorator(delegate);
            field.setValue(target, decorator);
        }
    }

    protected Object getInjectableReference(InjectionPointDescriptor injectionPoint) {
        return getInjectableReference(injectionPoint, false, extension.getStatefulScopes());
    }

    protected Object getInjectableReference(InjectionPointDescriptor injectionPoint, boolean readOnly,
            List<Class<? extends Annotation>> scopes) {
        String injectionPointName = injectionPoint.getName();
        AbstractManagedInstanceDescriptor candidate = registry.getManagedInstanceDescriptorByName(injectionPointName);

        Set<Bean<?>> beans = manager.getBeans(injectionPointName);
        Bean<?> bean = manager.resolve(beans);

        boolean create = injectionPoint.isCreate() || (candidate != null && candidate.isAutoCreate());

        for (Class<? extends Annotation> scope : scopes) {
            if (!CdiUtils.isContextActive(scope, manager)) {
                continue;
            }

            // check rewritable context
            Object reference = rewritableContextManager.get(injectionPointName, scope);
            if (reference != null) {
                return reference;
            }

            // check CDI read-only context
            if (bean != null && scope.equals(bean.getScope())) {
                Context context = manager.getContext(bean.getScope());
                Object value = context.get(bean);
                if (value != null) {
                    return value;
                }
            }
        }

        // create using CDI
        if (bean != null && !readOnly && (create || bean instanceof EntityProducer<?>)) {
            CreationalContext<?> ctx = manager.createCreationalContext(bean);
            Object product = manager.getReference(bean, Object.class, ctx);

            // void factory was invoked, let's find the outjected value
            if (product instanceof cz.muni.fi.xharting.classic.factory.Void) {
                // invoke void factory method
                ((cz.muni.fi.xharting.classic.factory.Void) product).forceBeanCreation();
                return getInjectableReference(injectionPoint, true, scopes);
            } else {
                return product;
            }
        }
        return null;
    }

    protected void outject(Object target, boolean enforce) {
        for (OutjectionPointDescriptor outjectionPoint : descriptor.getOutjectionPoints()) {
            outjectField(outjectionPoint, target, enforce);
        }
    }

    protected void outjectField(OutjectionPointDescriptor outjectionPoint, Object target, boolean enforce) {
        Object value = outjectionPoint.get(target);
        if (enforce && value == null && outjectionPoint.isRequired()) {
            throw new RequiredException("@Out attribute requires non-null value: " + outjectionPoint.getPath());
        }

        rewritableContextManager.set(outjectionPoint.getName(), value, outjectionPoint.getCdiScope());
    }

    protected void disinject(Object target) {
        for (InjectionPointDescriptor injectionPoint : descriptor.getInjectionPoints()) {
            injectionPoint.set(target, null);
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
