package cz.muni.fi.xharting.classic.bootstrap;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import cz.muni.fi.xharting.classic.metadata.BeanDescriptor.BeanType;
import cz.muni.fi.xharting.classic.metadata.ElFactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.ElObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.FactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.BeanDescriptor;
import cz.muni.fi.xharting.classic.metadata.ObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.RoleDescriptor;
import cz.muni.fi.xharting.classic.persistence.entity.DirectReferenceHolderBean;
import cz.muni.fi.xharting.classic.persistence.entity.EntityProducer;
import cz.muni.fi.xharting.classic.persistence.entity.PassivationCapableDirectReferenceHolderBean;
import cz.muni.fi.xharting.classic.persistence.entity.PassivationCapableEntityProducer;
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

    private final Map<Class<?>, AnnotatedType<?>> modifiedAnnotatedTypes = new HashMap<Class<?>, AnnotatedType<?>>();
    private final Set<AnnotatedType<?>> additionalAnnotatedTypes = new HashSet<AnnotatedType<?>>();
    private final Set<ObserverMethod<?>> observerMethodsToRegister = new HashSet<ObserverMethod<?>>();
    private final Set<Bean<?>> factoryMethodsToRegister = new HashSet<Bean<?>>();
    private final Set<UnwrappedBean> unwrappedBeansToRegister = new HashSet<UnwrappedBean>();
    private final Set<EntityProducer<?>> entities = new HashSet<EntityProducer<?>>();
    private final Set<DirectReferenceHolderBean<?>> entityHolders = new HashSet<DirectReferenceHolderBean<?>>();
    private final BeanManager manager;

    public ClassicBeanTransformer(ConditionalInstallationService service, BeanManager manager) {
        this(service.getInstallableManagedBeanBescriptors(), service.getInstallableFactoryDescriptors(), service
                .getInstallableObserverMethodDescriptors(), manager);
    }

    public ClassicBeanTransformer(Set<BeanDescriptor> managedBeanDescriptors,
            Set<AbstractFactoryDescriptor> factoryDescriptors, Set<AbstractObserverMethodDescriptor> observerMethods,
            BeanManager manager) {
        this.manager = manager;
        transformBeans(managedBeanDescriptors);
        transformFactories(factoryDescriptors);
        transformObserverMethods(observerMethods);
    }

    protected void transformBeans(Set<BeanDescriptor> beans) {
        for (BeanDescriptor bean : beans) {
            for (RoleDescriptor role : bean.getRoles()) {
                // entities require special treatment
                if (bean.getBeanType().equals(BeanType.ENTITY)) {
                    transformEntity(bean, role, bean.getJavaClass());
                    continue;
                }

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

                // We need the latter check since multiple xml-configured beans could share the same class
                if (!modifiedAnnotatedTypes.containsKey(bean.getJavaClass()) && bean.isDefinedByClass()) {
                    modifiedAnnotatedTypes.put(bean.getJavaClass(), builder.create());
                } else {
                    additionalAnnotatedTypes.add(builder.create());
                }
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

    protected void transformFactories(Set<AbstractFactoryDescriptor> factoryDescriptors) {
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

    protected void transformObserverMethods(Set<AbstractObserverMethodDescriptor> observerMethods) {

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

    protected <T> void transformEntity(BeanDescriptor bean, RoleDescriptor role, Class<T> javaClass) {
        if (!bean.getBeanType().equals(BeanType.ENTITY)) {
            throw new IllegalArgumentException(bean.getJavaClass() + " is not an entity.");
        }
        if (!bean.getInjectionPoints().isEmpty()) {
            throw new IllegalArgumentException("Entities cannot inject values"); // TODO DefinitionException
        }
        if (!bean.getOutjectionPoints().isEmpty()) {
            throw new IllegalArgumentException("Entities cannot outject values"); // TODO definitionException
        }
        if (!bean.getFactories().isEmpty()) {
            throw new IllegalArgumentException("Entities cannot define factory methods"); // TODO definitionException
        }
        if (bean.hasUnwrappingMethod()) {
            throw new IllegalArgumentException("Entities cannot define unwrap methods"); // TODO definitionException
        }

        DirectReferenceHolderBean<T> holder;
        EntityProducer<T> producer;
        if (Serializable.class.isAssignableFrom(javaClass)) {
            holder = new PassivationCapableDirectReferenceHolderBean<T>(javaClass, role.getName(), role.getCdiScope());
            producer = new PassivationCapableEntityProducer<T>(holder, manager, javaClass, role.getName());
        } else {
            holder = new DirectReferenceHolderBean<T>(javaClass, role.getName(), role.getCdiScope());
            producer = new EntityProducer<T>(holder, manager, javaClass, role.getName());
        }
        entityHolders.add(holder);
        entities.add(producer);
    }

    private <T> AnnotatedTypeBuilder<T> createAnnotatedTypeBuilder(Class<T> javaClass) {
        return new AnnotatedTypeBuilder<T>().readFromType(javaClass);
    }

    private <T> void registerInterceptors(BeanDescriptor descriptor, RoleDescriptor role, AnnotatedTypeBuilder<T> builder) {
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

    public Map<Class<?>, AnnotatedType<?>> getModifiedAnnotatedTypes() {
        return Collections.unmodifiableMap(modifiedAnnotatedTypes);
    }

    @SuppressWarnings("unchecked")
    public <T> AnnotatedType<T> getModifiedAnnotatedType(Class<T> clazz) {
        return (AnnotatedType<T>) modifiedAnnotatedTypes.get(clazz);
    }

    public Set<AnnotatedType<?>> getAdditionalAnnotatedTypes() {
        return additionalAnnotatedTypes;
    }

    public Set<ObserverMethod<?>> getObserverMethodsToRegister() {
        return observerMethodsToRegister;
    }

    public Set<Bean<?>> getFactoryMethodsToRegister() {
        return factoryMethodsToRegister;
    }

    public Set<EntityProducer<?>> getEntities() {
        return entities;
    }

    public Set<DirectReferenceHolderBean<?>> getEntityHolders() {
        return entityHolders;
    }
}
