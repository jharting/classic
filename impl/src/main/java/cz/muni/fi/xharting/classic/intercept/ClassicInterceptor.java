package cz.muni.fi.xharting.classic.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.InterceptionType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.solder.reflection.Reflections;

import cz.muni.fi.xharting.classic.util.spi.AbstractInterceptor;

@SuppressWarnings("deprecation")
public class ClassicInterceptor<T> extends AbstractInterceptor<T> {

    private final InjectionTarget<T> injectionTarget;
    private final Map<InterceptionType, Method> methods = new HashMap<InterceptionType, Method>();

    public ClassicInterceptor(AnnotatedType<T> interceptorType, BeanManager manager) {
        super(interceptorType.getJavaClass(), new ClassicInterceptorBinding.ClassicInterceptorBindingLiteral(
                interceptorType.getJavaClass()));
        this.injectionTarget = manager.createInjectionTarget(interceptorType);
        registerMethods();
    }

    private void registerMethods() {
        for (Method method : getBeanClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(AroundInvoke.class)
                    || method.isAnnotationPresent(org.jboss.seam.annotations.intercept.AroundInvoke.class)) {
                registerInterceptorMethod(method, InterceptionType.AROUND_INVOKE);
            }
            if (method.isAnnotationPresent(PostConstruct.class)
                    || method.isAnnotationPresent(org.jboss.seam.annotations.intercept.PostConstruct.class)) {
                registerInterceptorMethod(method, InterceptionType.POST_CONSTRUCT);
            }
            if (method.isAnnotationPresent(PreDestroy.class)) {
                registerInterceptorMethod(method, InterceptionType.PRE_DESTROY);
            }
            if (method.isAnnotationPresent(PostActivate.class)
                    || method.isAnnotationPresent(org.jboss.seam.annotations.intercept.PostActivate.class)) {
                registerInterceptorMethod(method, InterceptionType.POST_ACTIVATE);
            }
            if (method.isAnnotationPresent(PrePassivate.class)
                    || method.isAnnotationPresent(org.jboss.seam.annotations.intercept.PrePassivate.class)) {
                registerInterceptorMethod(method, InterceptionType.PRE_PASSIVATE);
            }
        }
        if (methods.isEmpty()) {
            throw new IllegalArgumentException("Interceptor " + getBeanClass() + " defines no interceptor methods.");
        }
    }

    private void registerInterceptorMethod(Method method, InterceptionType type) {
        if (methods.containsKey(type)) {
            throw new IllegalArgumentException("Interceptor " + getBeanClass() + " declares multiple " + type + " methods: "
                    + methods.get(type) + ", " + method);
        }
        if (method.getParameterTypes().length != 1) {
            throw new IllegalArgumentException("Interceptor method " + method + " has incorrect number of parameters "
                    + method.getParameterTypes().length);
        }
        Class<?> invocationContextType = method.getParameterTypes()[0];
        if (!InvocationContext.class.equals(invocationContextType)
                && !org.jboss.seam.intercept.InvocationContext.class.equals(invocationContextType)) {
            throw new IllegalArgumentException("Interceptor method" + method + " has incorrect parameter type "
                    + method.getParameterTypes()[0]);
        }

        switch (type) {
            case AROUND_INVOKE:
            case AROUND_TIMEOUT:
                if (!Object.class.equals(method.getReturnType())) {
                    throw new IllegalArgumentException("Interceptor method" + method + " has incorrect return type "
                            + method.getReturnType());
                }
                break;
            default: // @Pre* and @Post* methods have different method signature
                if (!void.class.equals(method.getReturnType())) {
                    throw new IllegalArgumentException("Interceptor method" + method + " has incorrect return type "
                            + method.getReturnType());
                }
        }
        methods.put(type, method);
    }

    @Override
    public boolean intercepts(InterceptionType type) {
        boolean result = methods.containsKey(type);
        return result;
    }

    @Override
    public Object intercept(InterceptionType type, T instance, InvocationContext ctx) {
        Method method = methods.get(type);

        if (method == null) {
            throw new IllegalStateException("Interceptor " + getBeanClass() + " should not be called for " + type
                    + " interception type.");
        }

        return Reflections.invokeMethod(method, instance, new LegacyInvocationContext(ctx));
    }

    @Override
    public T create(CreationalContext<T> creationalContext) {
        // just in case, we could simply create new instance instead
        T instance = injectionTarget.produce(creationalContext);
        injectionTarget.inject(instance, creationalContext);
        return instance;
    }

    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        creationalContext.release();
    }
}
