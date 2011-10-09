package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.classic.init.event.LegacyElObserverMethod;
import org.jboss.seam.classic.init.event.LegacyObserverMethod;
import org.jboss.seam.classic.init.factory.LegacyFactory;
import org.jboss.seam.classic.init.factory.LegacyElFactory;
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
import org.jboss.seam.classic.runtime.BijectionInterceptor;
import org.jboss.seam.classic.util.CdiScopeUtils;
import org.jboss.seam.solder.literal.NamedLiteral;
import org.jboss.seam.solder.reflection.annotated.AnnotatedTypeBuilder;

/**
 * This class is responsible for transforming metadata gathered during the scanning phase to CDI SPI objects.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ClassicBeanTransformer {

    private Set<AnnotatedType<?>> annotatedTypesToRegister = new HashSet<AnnotatedType<?>>();
    private Set<ObserverMethod<?>> observerMethodsToRegister = new HashSet<ObserverMethod<?>>();

    public void processLegacyBeans(Set<ManagedBeanDescriptor> beans, BeanManager manager) {
        for (ManagedBeanDescriptor bean : beans) {
            for (RoleDescriptor role : bean.getRoles()) {
                AnnotatedTypeBuilder<?> builder = createAnnotatedTypeBuilder(bean.getJavaClass());
                // Set name
                builder.addToClass(new NamedLiteral(role.getName()));
                // Set scope
                Class<? extends Annotation> scope = role.getCdiScope();
                builder.addToClass(CdiScopeUtils.getScopeLiteral(scope));
                // Process annotation redefiners
                builder.redefine(Create.class, new CreateAnnotationRedefiner());
                builder.redefine(Destroy.class, new DestroyAnnotationRedefiner());

                // TODO observer methods
                // TODO interceptors
                // TODO producer fields

                // Register interceptors
                // TODO: take @BypassInterceptors into account
                registerInterceptors(builder);

                annotatedTypesToRegister.add(builder.create());
            }
        }
    }

    public Set<Bean<?>> processLegacyFactories(Set<AbstractFactoryDescriptor> factoryDescriptors, BeanManager manager) {
        Set<Bean<?>> factories = new HashSet<Bean<?>>();
        for (AbstractFactoryDescriptor descriptor : factoryDescriptors) {
            if (descriptor instanceof FactoryDescriptor) {
                FactoryDescriptor beanFactoryDescriptor = (FactoryDescriptor) descriptor;
                factories.add(createClassicFactory(beanFactoryDescriptor, beanFactoryDescriptor.getProductType(), manager));
            } else if (descriptor instanceof ElFactoryDescriptor) {
                ElFactoryDescriptor factoryDescriptor = (ElFactoryDescriptor) descriptor;
                factories.add(new LegacyElFactory(factoryDescriptor, manager));
            }
        }
        return factories;
    }

    public Set<ObserverMethod<?>> processLegacyObserverMethods(Set<AbstractObserverMethodDescriptor> observerMethods,
            BeanManager manager) {

        for (AbstractObserverMethodDescriptor om : observerMethods) {
            for (TransactionPhase phase : new TransactionPhase[] { TransactionPhase.IN_PROGRESS,
                    TransactionPhase.AFTER_COMPLETION, TransactionPhase.AFTER_SUCCESS }) {

                if (om instanceof ElObserverMethodDescriptor) {
                    ElObserverMethodDescriptor observerMethod = (ElObserverMethodDescriptor) om;
                    observerMethodsToRegister.add(new LegacyElObserverMethod(observerMethod, phase,  manager));
                }
                if (om instanceof ObserverMethodDescriptor) {
                    ObserverMethodDescriptor observerMethod = (ObserverMethodDescriptor) om;
                    for (RoleDescriptor role : observerMethod.getBean().getRoles()) {
                        observerMethodsToRegister.add(new LegacyObserverMethod(role.getName(), observerMethod, phase, manager));
                    }
                }
            }
        }
        return observerMethodsToRegister;
    }

    private <T> LegacyFactory<T> createClassicFactory(FactoryDescriptor descriptor, Class<T> beanClass,
            BeanManager manager) {
        return new LegacyFactory<T>(descriptor, beanClass, manager);
    }

    private <T> AnnotatedTypeBuilder<T> createAnnotatedTypeBuilder(Class<T> javaClass) {
        return new AnnotatedTypeBuilder<T>().readFromType(javaClass);
    }

    private <T> void registerInterceptors(AnnotatedTypeBuilder<T> builder) {
        builder.addToClass(BijectionInterceptor.Bijected.BijectedLiteral.INSTANCE);
    }

    public Set<AnnotatedType<?>> getAnnotatedTypesToRegister() {
        return annotatedTypesToRegister;
    }

    public Set<ObserverMethod<?>> getObserverMethodsToRegister() {
        return observerMethodsToRegister;
    }
}
