package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.persistence.Entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.classic.init.metadata.BeanDescriptor;
import org.jboss.seam.classic.init.metadata.RoleDescriptor;
import org.jboss.seam.classic.init.redefiners.CreateAnnotationRedefiner;
import org.jboss.seam.classic.init.redefiners.DestroyAnnotationRedefiner;
import org.jboss.seam.solder.literal.ApplicationScopedLiteral;
import org.jboss.seam.solder.literal.ConversationScopedLiteral;
import org.jboss.seam.solder.literal.DependentLiteral;
import org.jboss.seam.solder.literal.NamedLiteral;
import org.jboss.seam.solder.literal.RequestScopedLiteral;
import org.jboss.seam.solder.literal.SessionScopedLiteral;
import org.jboss.seam.solder.reflection.annotated.AnnotatedTypeBuilder;

public class ClassicBeanTransformer {

    private Set<AnnotatedType<?>> annotatedTypesToRegister = new HashSet<AnnotatedType<?>>();
    private Set<Bean<?>> producerMethodsToRegister = new HashSet<Bean<?>>();
    private Set<ObserverMethod<?>> observerMethodsToRegister = new HashSet<ObserverMethod<?>>();
    
    public void processLegacyBeans(Set<BeanDescriptor> beans) {
        // TODO process @Install

        for (BeanDescriptor bean : beans) {
            for (RoleDescriptor role : bean.getRoles()) {
                AnnotatedTypeBuilder<?> builder = createAnnotatedTypeBuilder(bean.getJavaClass());
                // Set name
                builder.addToClass(new NamedLiteral(role.getName()));
                // Set scope
                Annotation scope = transformScope(role.getScope(), bean.getJavaClass());
                builder.addToClass(scope);
                // Process annotation redefiners
                builder.redefine(Create.class, new CreateAnnotationRedefiner());
                builder.redefine(Destroy.class, new DestroyAnnotationRedefiner());

                // TODO producer methods
                // TODO observer methods
                // TODO interceptors
                
                annotatedTypesToRegister.add(builder.create());
            }
        }
    }

    private <T> AnnotatedTypeBuilder<T> createAnnotatedTypeBuilder(Class<T> javaClass) {
        return new AnnotatedTypeBuilder<T>().readFromType(javaClass);
    }

    private Annotation transformScope(ScopeType scope, Class<?> javaClass) {
        switch (scope) {
            case STATELESS:
                return new DependentLiteral();
            case METHOD:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            case EVENT:
                return new RequestScopedLiteral();
            case PAGE:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            case CONVERSATION:
                return new ConversationScopedLiteral();
            case SESSION:
                return new SessionScopedLiteral();
            case APPLICATION:
                return new ApplicationScopedLiteral();
            case BUSINESS_PROCESS:
                throw new UnsupportedOperationException("Scope not supported"); // TODO
            case UNSPECIFIED:
                if (javaClass.isAnnotationPresent(Stateful.class) || javaClass.isAnnotationPresent(Entity.class)) {
                    return new ConversationScopedLiteral();
                } else if (javaClass.isAnnotationPresent(Stateless.class)) {
                    return new DependentLiteral();
                } else {
                    return new RequestScopedLiteral();
                }
            default:
                throw new IllegalStateException();
        }
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
