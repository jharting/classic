package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.ObserverMethod;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.classic.init.metadata.BeanDescriptor;
import org.jboss.seam.classic.init.metadata.FactoryDescriptor;
import org.jboss.seam.classic.init.metadata.RoleDescriptor;
import org.jboss.seam.classic.init.redefiners.CreateAnnotationRedefiner;
import org.jboss.seam.classic.init.redefiners.DestroyAnnotationRedefiner;
import org.jboss.seam.classic.runtime.BijectionInterceptor;
import org.jboss.seam.classic.util.CdiScopeUtils;
import org.jboss.seam.classic.util.ClassicScopeUtils;
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
    private Set<Bean<?>> producerMethodsToRegister = new HashSet<Bean<?>>();
    private Set<ObserverMethod<?>> observerMethodsToRegister = new HashSet<ObserverMethod<?>>();

    public void processLegacyBeans(Set<BeanDescriptor> beans, BeanManager manager) {
        // TODO process @Install

        for (BeanDescriptor bean : beans) {
            for (RoleDescriptor role : bean.getRoles()) {
                AnnotatedTypeBuilder<?> builder = createAnnotatedTypeBuilder(bean.getJavaClass());
                // Set name
                builder.addToClass(new NamedLiteral(role.getName()));
                // Set scope
                Class<? extends Annotation> scope = ClassicScopeUtils.transformLegacyBeanScopeToCdiScope(role.getScope(), bean.getJavaClass());
                builder.addToClass(CdiScopeUtils.getScopeLiteral(scope));
                // Process annotation redefiners
                builder.redefine(Create.class, new CreateAnnotationRedefiner());
                builder.redefine(Destroy.class, new DestroyAnnotationRedefiner());

                for (FactoryDescriptor factory : bean.getFactories()) {
                    Class<? extends Annotation> factoryScope = ClassicScopeUtils.transformLegacyFactoryScopeToCdiScope(factory.getScope(),
                            role.getScope(), bean.getJavaClass());
                    producerMethodsToRegister.add(createClassicFactory(factory, factory.getProductType(), factoryScope,
                            role.getName(), manager));
                }

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

    private <T> ClassicFactory<T> createClassicFactory(FactoryDescriptor descriptor, Class<T> beanClass,
            Class<? extends Annotation> scope, String hostName, BeanManager manager) {
        return new ClassicFactory<T>(descriptor, beanClass, scope, hostName, manager);
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

    public Set<Bean<?>> getProducerMethodsToRegister() {
        return producerMethodsToRegister;
    }

    public Set<ObserverMethod<?>> getObserverMethodsToRegister() {
        return observerMethodsToRegister;
    }
}
