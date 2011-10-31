package cz.muni.fi.xharting.classic.bootstrap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import org.jboss.solder.logging.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cz.muni.fi.xharting.classic.bootstrap.scan.ReflectionsScanner;
import cz.muni.fi.xharting.classic.bootstrap.scan.Scanner;
import cz.muni.fi.xharting.classic.config.ConfigurationService;
import cz.muni.fi.xharting.classic.config.ConfiguringInjectionTarget;
import cz.muni.fi.xharting.classic.factory.UnwrappedBean;
import cz.muni.fi.xharting.classic.metadata.BeanDescriptor;
import cz.muni.fi.xharting.classic.metadata.MetadataRegistry;
import cz.muni.fi.xharting.classic.metadata.NamespaceDescriptor;
import cz.muni.fi.xharting.classic.persistence.entity.DirectReferenceHolderBean;
import cz.muni.fi.xharting.classic.persistence.entity.EntityProducer;

public class CoreExtension implements Extension {

    private static final Logger log = Logger.getLogger(CoreExtension.class);

    private ClassicBeanTransformer beanTransformer;

    private MetadataRegistry registry;
    private ConfigurationService configuration;

    void scan(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        log.debug("Scanning for Seam 2 beans.");
        Scanner scanner = new ReflectionsScanner(this.getClass().getClassLoader());
        scanner.scan();
        manager.fireEvent(new ScanningCompleteEvent(scanner, event));
    }

    void init(@Observes ScanningCompleteEvent event, BeanManager manager) {

        configuration = new ConfigurationService(event.getScanner());

        // process found components
        Set<Class<?>> classes = event.getScanner().getTypesAnnotatedWith(Name.class);
        Multimap<String, BeanDescriptor> discoveredManagedBeanDescriptors = HashMultimap.create();
        for (Class<?> clazz : classes) {
            BeanDescriptor beanDescriptor = new BeanDescriptor(clazz);
            discoveredManagedBeanDescriptors.put(beanDescriptor.getImplicitRole().getName(), beanDescriptor);
        }

        // process namespaces so that we can load XML configuration
        Set<Class<?>> namespaceAnnotations = event.getScanner().getTypesAnnotatedWith(Namespace.class);
        Map<String, NamespaceDescriptor> namespaces = registerNamespaces(namespaceAnnotations);

        // process XML configuration
        configuration.loadConfiguration(namespaces);
        Multimap<String, BeanDescriptor> managedBeanDescriptors = configuration
                .mergeManagedBeanConfiguration(discoveredManagedBeanDescriptors);

        // process conditional installation
        ConditionalInstallationService installationService = new ConditionalInstallationService(
                managedBeanDescriptors.values(), configuration.getFactories(), configuration.getObserverMethods());
        installationService.filterInstallableComponents();
        beanTransformer = new ClassicBeanTransformer(installationService, manager);

        // register annotated types for managed beans
        log.debugv("Registering {0} additional annotated types.", beanTransformer.getAdditionalAnnotatedTypes().size());
        for (AnnotatedType<?> annotatedType : beanTransformer.getAdditionalAnnotatedTypes()) {
            log.debugv("Registering {0}", annotatedType.getAnnotation(Named.class).value());
            event.addAnnotatedType(annotatedType);
        }

        // make metamodel accessible at runtime
        registry = new MetadataRegistry(installationService);
    }

    /**
     * We cannot simply veto all beans and register our ones since that does not work for EJBs.
     */
    <T> void modifyAnnotatedTypes(@Observes ProcessAnnotatedType<T> event) {
        if (event.getAnnotatedType().isAnnotationPresent(Name.class)) {
            AnnotatedType<T> type = event.getAnnotatedType();
            AnnotatedType<T> modifiedType = beanTransformer.getModifiedAnnotatedType(type.getJavaClass());
            if (modifiedType != null) {
                event.setAnnotatedType(modifiedType);
            } else {
                event.veto();
            }
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

    void registerEntities(@Observes AfterBeanDiscovery event) {
        for (DirectReferenceHolderBean<?> holder : beanTransformer.getEntityHolders()) {
            event.addBean(holder);
        }
        for (EntityProducer<?> entity : beanTransformer.getEntities()) {
            event.addBean(entity);
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

    protected Map<String, NamespaceDescriptor> registerNamespaces(Collection<Class<?>> packages) {
        Map<String, NamespaceDescriptor> namespaces = new HashMap<String, NamespaceDescriptor>();
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
        return namespaces;
    }

    public MetadataRegistry getRegistry() {
        return registry;
    }
}
