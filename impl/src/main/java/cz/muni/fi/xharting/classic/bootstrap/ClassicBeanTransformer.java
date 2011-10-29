package cz.muni.fi.xharting.classic.bootstrap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.interceptor.InterceptorBinding;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.intercept.Interceptors;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.solder.literal.DefaultLiteral;
import org.jboss.solder.literal.NamedLiteral;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;

import cz.muni.fi.xharting.classic.Seam2ManagedBean;
import cz.muni.fi.xharting.classic.bijection.BijectionInterceptor;
import cz.muni.fi.xharting.classic.bootstrap.redefiners.CreateAnnotationRedefiner;
import cz.muni.fi.xharting.classic.bootstrap.redefiners.DestroyAnnotationRedefiner;
import cz.muni.fi.xharting.classic.bootstrap.redefiners.LoggerRedefiner;
import cz.muni.fi.xharting.classic.bootstrap.redefiners.RequestParameterRedefiner;
import cz.muni.fi.xharting.classic.event.LegacyElObserverMethod;
import cz.muni.fi.xharting.classic.event.LegacyObserverMethod;
import cz.muni.fi.xharting.classic.factory.LegacyElFactory;
import cz.muni.fi.xharting.classic.factory.LegacyFactory;
import cz.muni.fi.xharting.classic.factory.UnwrappedBean;
import cz.muni.fi.xharting.classic.metadata.AbstractFactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.AbstractObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.ElFactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.ElObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.FactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.ManagedBeanDescriptor;
import cz.muni.fi.xharting.classic.metadata.ObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.RoleDescriptor;
import cz.muni.fi.xharting.classic.scope.page.PageScoped;
import cz.muni.fi.xharting.classic.util.CdiScopeUtils;
import cz.muni.fi.xharting.classic.util.literal.SynchronizedLiteral;

/**
 * This class is responsible for transforming metadata gathered during the scanning phase to CDI SPI objects.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ClassicBeanTransformer {

    private Set<AnnotatedType<?>> annotatedTypesToRegister = new HashSet<AnnotatedType<?>>();
    private Set<ObserverMethod<?>> observerMethodsToRegister = new HashSet<ObserverMethod<?>>();
    private Set<Bean<?>> factoryMethodsToRegister = new HashSet<Bean<?>>();
    private Set<UnwrappedBean> unwrappedBeansToRegister = new HashSet<UnwrappedBean>();

    public ClassicBeanTransformer(ConditionalInstallationService service, BeanManager manager) {
        this(service.getInstallableManagedBeanBescriptors(), service.getInstallableFactoryDescriptors(), service
                .getInstallableObserverMethodDescriptors(), manager);
    }

    public ClassicBeanTransformer(Set<ManagedBeanDescriptor> managedBeanDescriptors,
            Set<AbstractFactoryDescriptor> factoryDescriptors, Set<AbstractObserverMethodDescriptor> observerMethods,
            BeanManager manager) {
        transformBeans(managedBeanDescriptors, manager);
        transformFactories(factoryDescriptors, manager);
        transformObserverMethods(observerMethods, manager);
    }

    protected void transformBeans(Set<ManagedBeanDescriptor> beans, BeanManager manager) {
        for (ManagedBeanDescriptor bean : beans) {
            for (RoleDescriptor role : bean.getRoles()) {
                AnnotatedTypeBuilder<?> builder = createAnnotatedTypeBuilder(bean.getJavaClass());
                // Set name
                builder.addToClass(new Seam2ManagedBean.Seam2ManagedBeanLiteral(role.getName()));
                builder.addToClass(DefaultLiteral.INSTANCE);
                if (bean.hasUnwrappingMethod()) // if it has one, the name is reserved for the unwrapping method
                {
                    registerUnwrappedBean(role.getName(), bean.getJavaClass(), bean.getUnwrappingMethod()
                            .getGenericReturnType(), bean.getUnwrappingMethod(), manager);
                } else {
                    builder.addToClass(new NamedLiteral(role.getName()));
                }
                // Set scope
                Class<? extends Annotation> scope = role.getCdiScope();
                builder.addToClass(CdiScopeUtils.getScopeLiteral(scope));

                // Process annotation redefiners
                // Lifecycle event interceptor methods
                builder.redefine(Create.class, new CreateAnnotationRedefiner());
                builder.redefine(Destroy.class, new DestroyAnnotationRedefiner());
                // Special injection points
                builder.redefine(RequestParameter.class, new RequestParameterRedefiner());
                builder.redefine(Logger.class, new LoggerRedefiner());

                // @BypassInterceptors
                if (builder.getJavaClass().isAnnotationPresent(BypassInterceptors.class)) {
                    removeInterceptorBindings(builder);
                }
                // Register interceptors
                else {
                    registerInterceptors(bean, role, builder);
                }
                annotatedTypesToRegister.add(builder.create());
            }
        }
    }

    /**
     * Removes all CDI and Seam 2 interceptor bindings from the class as well as all its methods.
     */
    protected void removeInterceptorBindings(AnnotatedTypeBuilder<?> builder) {
        Class<?> javaClass = builder.getJavaClass();
        for (Annotation annotation : javaClass.getAnnotations()) {
            if (isInterceptorBinding(annotation)) {
                builder.removeFromClass(annotation.annotationType());
            }
        }
        for (Class<?> clazz = javaClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    if (isInterceptorBinding(annotation)) {
                        builder.removeFromMethod(method, annotation.annotationType());
                    }
                }
            }
        }
    }

    /**
     * Returns true if a given annotation is either CDI or Seam 2 interceptor binding.
     */
    protected boolean isInterceptorBinding(Annotation annotation) {
        Class<? extends Annotation> annotationClass = annotation.annotationType();
        return annotationClass.isAnnotationPresent(InterceptorBinding.class)
                || annotationClass.isAnnotationPresent(Interceptors.class) || annotationClass.equals(Interceptors.class);
    }

    protected void transformFactories(Set<AbstractFactoryDescriptor> factoryDescriptors, BeanManager manager) {
        for (AbstractFactoryDescriptor descriptor : factoryDescriptors) {
            if (descriptor instanceof FactoryDescriptor) {
                FactoryDescriptor beanFactoryDescriptor = (FactoryDescriptor) descriptor;
                factoryMethodsToRegister.add(new LegacyFactory(beanFactoryDescriptor, manager));
            } else if (descriptor instanceof ElFactoryDescriptor) {
                ElFactoryDescriptor factoryDescriptor = (ElFactoryDescriptor) descriptor;
                factoryMethodsToRegister.add(new LegacyElFactory(factoryDescriptor, manager));
            }
        }
    }

    protected void transformObserverMethods(Set<AbstractObserverMethodDescriptor> observerMethods, BeanManager manager) {

        for (AbstractObserverMethodDescriptor om : observerMethods) {
            for (TransactionPhase phase : new TransactionPhase[] { TransactionPhase.IN_PROGRESS,
                    TransactionPhase.AFTER_COMPLETION, TransactionPhase.AFTER_SUCCESS }) {

                if (om instanceof ElObserverMethodDescriptor) {
                    ElObserverMethodDescriptor observerMethod = (ElObserverMethodDescriptor) om;
                    observerMethodsToRegister.add(new LegacyElObserverMethod(observerMethod, phase, manager));
                }
                if (om instanceof ObserverMethodDescriptor) {
                    ObserverMethodDescriptor observerMethod = (ObserverMethodDescriptor) om;
                    for (RoleDescriptor role : observerMethod.getBean().getRoles()) {
                        observerMethodsToRegister.add(new LegacyObserverMethod(role.getName(), observerMethod, phase, manager));
                    }
                }
            }
        }
    }

    private <T> AnnotatedTypeBuilder<T> createAnnotatedTypeBuilder(Class<T> javaClass) {
        return new AnnotatedTypeBuilder<T>().readFromType(javaClass);
    }

    private <T> void registerInterceptors(ManagedBeanDescriptor descriptor, RoleDescriptor role, AnnotatedTypeBuilder<T> builder) {
        // support for injection/outjection
        builder.addToClass(BijectionInterceptor.Bijected.BijectedLiteral.INSTANCE);
        // session-scoped and page-scoped components are synchronized automatically
        if (SessionScoped.class.equals(role.getCdiScope()) || PageScoped.class.equals(role.getCdiScope())) {
            builder.addToClass(SynchronizedLiteral.DEFAULT_INSTANCE);
        }

    }

    public Set<UnwrappedBean> getUnwrappedBeansToRegister() {
        return unwrappedBeansToRegister;
    }

    private <T> void registerUnwrappedBean(String name, Class<?> hostType, Type type, Method method, BeanManager manager) {
        unwrappedBeansToRegister.add(new UnwrappedBean(name, hostType, type, method, manager));
    }

    public Set<AnnotatedType<?>> getAnnotatedTypesToRegister() {
        return annotatedTypesToRegister;
    }

    public Set<ObserverMethod<?>> getObserverMethodsToRegister() {
        return observerMethodsToRegister;
    }

    public Set<Bean<?>> getFactoryMethodsToRegister() {
        return factoryMethodsToRegister;
    }
}
