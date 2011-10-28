package org.jboss.seam.classic.init;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.inject.Named;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Namespace;
import org.jboss.seam.classic.config.ConfigurationService;
import org.jboss.seam.classic.config.ConfiguringInjectionTarget;
import org.jboss.seam.classic.init.factory.UnwrappedBean;
import org.jboss.seam.classic.init.metadata.ManagedBeanDescriptor;
import org.jboss.seam.classic.init.metadata.MetadataRegistry;
import org.jboss.seam.classic.init.metadata.NamespaceDescriptor;
import org.jboss.seam.classic.init.scan.ReflectionsScanner;
import org.jboss.seam.classic.init.scan.Scanner;
import org.jboss.seam.classic.runtime.outjection.OutjectedReferenceHolder;
import org.jboss.seam.classic.util.CdiScopeUtils;
import org.jboss.solder.core.Veto;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SeamClassicExtension implements Extension {

    private static final Logger log = Logger.getLogger(SeamClassicExtension.class);

    private final Map<String, NamespaceDescriptor> namespaces = new HashMap<String, NamespaceDescriptor>();

    private ClassicBeanTransformer beanTransformer;

    private MetadataRegistry registry;
    private ConfigurationService configuration = new ConfigurationService();

    void init(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        log.debug("Scanning for Seam 2 beans.");
        Scanner scanner = new ReflectionsScanner(this.getClass().getClassLoader());
        scanner.scan();

        manager.fireEvent(new ScanningCompleteEvent(scanner, event));

        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(Name.class);
        Set<Class<?>> namespaces = scanner.getTypesAnnotatedWith(Namespace.class);
        registerNamespaces(namespaces);
        log.debugv("Scan finished. {0} classes were loaded. {1} namespace declaring packages were loaded.", classes.size(),
                namespaces.size());

        Multimap<String, ManagedBeanDescriptor> discoveredManagedBeanDescriptors = HashMultimap.create();
        for (Class<?> clazz : classes) {
            ManagedBeanDescriptor beanDescriptor = new ManagedBeanDescriptor(clazz);
            discoveredManagedBeanDescriptors.put(beanDescriptor.getImplicitRole().getName(), beanDescriptor);
        }

        configuration.loadConfiguration(this.namespaces);

        Multimap<String, ManagedBeanDescriptor> managedBeanDescriptors = configuration.mergeManagedBeanConfiguration(discoveredManagedBeanDescriptors);
        
        ConditionalInstallationService installationService = new ConditionalInstallationService(
                managedBeanDescriptors.values(), configuration.getFactories(), configuration.getObserverMethods());
        installationService.filterInstallableComponents();

        registry = new MetadataRegistry(installationService);
        beanTransformer = new ClassicBeanTransformer(installationService, manager);

        // register annotated types for managed beans
        log.debugv("Registering {0} annotated types.", beanTransformer.getAnnotatedTypesToRegister().size());
        for (AnnotatedType<?> annotatedType : beanTransformer.getAnnotatedTypesToRegister()) {
            log.debugv("Registering {0}", annotatedType.getJavaClass());
            event.addAnnotatedType(annotatedType);
        }
    }

    void vetoClassicBeans(@Observes ProcessAnnotatedType<?> event) {
        // We transform and register every Seam 2 component during BBD.
        // If a class get scanned by CDI as well, veto it.
        if (event.getAnnotatedType().isAnnotationPresent(Name.class)) {
            event.veto();
        }
    }

    void registerFactories(@Observes AfterBeanDiscovery event, BeanManager manager) {
        log.debugv("Registering {0} factories.", beanTransformer.getFactoryMethodsToRegister().size());
        for (Bean<?> factory : beanTransformer.getFactoryMethodsToRegister()) {
            log.debugv("Registering {0}", factory);
            event.addBean(factory);
        }
        Set<UnwrappedBean> unwrappedBeans = beanTransformer.getUnwrappedBeansToRegister();
        log.debugv("Registering {0} unwrapping methods.", unwrappedBeans.size());
        for (UnwrappedBean unwrappedBean : unwrappedBeans) {
            log.debugv("Registering {0}", unwrappedBean);
            event.addBean(unwrappedBean);
        }

    }

    void registerObserverMethods(@Observes AfterBeanDiscovery event, BeanManager manager) {
        log.debugv("Registering {0} observer methods.", beanTransformer.getObserverMethodsToRegister().size());
        for (ObserverMethod<?> observerMethod : beanTransformer.getObserverMethodsToRegister()) {
            log.debugv("Registering {0}", observerMethod);
            event.addObserverMethod(observerMethod);
        }
    }

    <T> void registerConfiguringInjectionTargets(@Observes ProcessInjectionTarget<T> event) {
        Named named = event.getAnnotatedType().getAnnotation(Named.class);
        if (named != null && configuration.getInitialValueMap().containsKey(named.value())) {
            InjectionTarget<T> delegate = event.getInjectionTarget();
            AnnotatedType<T> annotatedType = event.getAnnotatedType();
            InjectionTarget<T> replacement = new ConfiguringInjectionTarget<T>(configuration.getInitialValueMap().get(
                    named.value()), delegate, annotatedType, named.value());
            event.setInjectionTarget(replacement);
        }
    }

    void registerOutjectedReferenceHolders(@Observes BeforeBeanDiscovery event, BeanManager manager) {
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

    private void registerNamespaces(Collection<Class<?>> packages) {
        for (Class<?> pkg : packages) {
            Namespace namespaceAnnotation = pkg.getAnnotation(Namespace.class);
            if (namespaceAnnotation == null) {
                throw new IllegalStateException("Namespace-scanned package does not define namespace.");
            }
            String namespace = namespaceAnnotation.value();
            if (namespaces.containsKey(namespace)) {
                NamespaceDescriptor descriptor = namespaces.get(namespace);
                descriptor.addPackageName(pkg.getPackage().getName());
            } else {
                NamespaceDescriptor descriptor = new NamespaceDescriptor(namespaceAnnotation, pkg.getPackage());
                namespaces.put(namespace, descriptor);
            }
        }
    }

    public MetadataRegistry getRegistry() {
        return registry;
    }
}
