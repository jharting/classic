package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.classic.init.event.LegacyElObserverMethod;
import org.jboss.seam.classic.init.event.LegacyObserverMethod;
import org.jboss.seam.classic.init.factory.LegacyFactory;
import org.jboss.seam.classic.init.factory.LegacyElFactory;
import org.jboss.seam.classic.init.factory.UnwrappedBean;
import org.jboss.seam.classic.init.metadata.AbstractFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.AbstractObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.FactoryDescriptor;
import org.jboss.seam.classic.init.metadata.ElFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.ElObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.ManagedBeanDescriptor;
import org.jboss.seam.classic.init.metadata.ObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.RoleDescriptor;
import org.jboss.seam.classic.init.redefiners.CreateAnnotationRedefiner;
import org.jboss.seam.classic.init.redefiners.DestroyAnnotationRedefiner;
import org.jboss.seam.classic.init.redefiners.LoggerRedefiner;
import org.jboss.seam.classic.init.redefiners.RequestParameterRedefiner;
import org.jboss.seam.classic.runtime.BijectionInterceptor;
import org.jboss.seam.classic.util.CdiScopeUtils;
import org.jboss.solder.literal.DefaultLiteral;
import org.jboss.solder.literal.NamedLiteral;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;
import org.jboss.seam.classic.Seam2ManagedBean;

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

                // TODO interceptors

                // Register interceptors
                // TODO: take @BypassInterceptors into account
                registerInterceptors(builder);

                annotatedTypesToRegister.add(builder.create());
            }
        }
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

    private <T> void registerInterceptors(AnnotatedTypeBuilder<T> builder) {
        builder.addToClass(BijectionInterceptor.Bijected.BijectedLiteral.INSTANCE);
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
