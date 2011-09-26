package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.classic.init.metadata.BeanDescriptor;
import org.jboss.seam.classic.init.scan.ScannotationScanner;
import org.jboss.seam.classic.runtime.outjection.OutjectedReferenceHolder;
import org.jboss.seam.classic.util.CdiScopeUtils;
import org.jboss.seam.logging.Logger;
import org.jboss.seam.solder.core.Veto;
import org.jboss.seam.solder.reflection.annotated.AnnotatedTypeBuilder;

public class SeamClassicExtension implements Extension {

    private static final Logger log = Logger.getLogger(SeamClassicExtension.class);

    private Set<BeanDescriptor> descriptors = new HashSet<BeanDescriptor>();

    private ClassicBeanTransformer beanTransformer = new ClassicBeanTransformer();

    public void init(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        log.debug("Scanning for Seam 2 beans.");
        ScannotationScanner scanner = new ScannotationScanner(this.getClass().getClassLoader());
        scanner.scan();
        Set<Class<?>> classes = scanner.getClasses(Name.class.getName());
        log.debugv("Scan finished. {0} classes were loaded.", classes.size());

        Set<BeanDescriptor> enabledDescriptors = new HashSet<BeanDescriptor>();
        for (Class<?> clazz : classes) {
            enabledDescriptors.add(new BeanDescriptor(clazz));
        }

        // TODO: reduce allDescriptors by processing @Install

        descriptors = enabledDescriptors;

        // TODO: process XML configuration

        beanTransformer.processLegacyBeans(descriptors, manager);
        Set<AnnotatedType<?>> annotatedTypes = beanTransformer.getAnnotatedTypesToRegister();
        log.debugv("Registering {0} annotated types.", annotatedTypes.size());
        for (AnnotatedType<?> annotatedType : annotatedTypes) {
            log.debugv("Registering {0}", annotatedType.getJavaClass());
            event.addAnnotatedType(annotatedType);
        }
    }

    public void vetoClassicBeans(@Observes ProcessAnnotatedType<?> event) {
        // We transform and register every Seam 2 component during BBD.
        // If a class get scanned by CDI as well, veto it.
        if (event.getAnnotatedType().isAnnotationPresent(Name.class)) {
            event.veto();
        }
    }

    public void registerBeans(@Observes AfterBeanDiscovery event) {
        Set<Bean<?>> factories = beanTransformer.getProducerMethodsToRegister();
        log.debugv("Registering {0} factories.", factories.size());
        for (Bean<?> factory : factories) {
            log.debugv("Registering {0}", factory);
            event.addBean(factory);
        }
    }

    public Set<BeanDescriptor> getDescriptors() {
        return descriptors;
    }

    public void registerOutjectedReferenceHolders(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        event.addAnnotatedType(createOutjectedReferenceHolder(RequestScoped.class));
        event.addAnnotatedType(createOutjectedReferenceHolder(ConversationScoped.class));
        event.addAnnotatedType(createOutjectedReferenceHolder(SessionScoped.class));
        event.addAnnotatedType(createOutjectedReferenceHolder(ApplicationScoped.class));
    }

    private AnnotatedType<?> createOutjectedReferenceHolder(Class<? extends Annotation> scope) {
        AnnotatedTypeBuilder<OutjectedReferenceHolder> builder = new AnnotatedTypeBuilder<OutjectedReferenceHolder>();
        builder.readFromType(OutjectedReferenceHolder.class);
        builder.addToClass(CdiScopeUtils.getScopeLiteral(scope));
        builder.addToClass(OutjectedReferenceHolder.ScopeQualifier.ScopeQualifierLiteral.valueOf(scope));
        builder.removeFromClass(Veto.class);
        return builder.create();
    }
}
