package org.jboss.seam.classic.init;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
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
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.classic.config.ComponentsDotXml;
import org.jboss.seam.classic.init.factory.UnwrappedBean;
import org.jboss.seam.classic.init.metadata.AbstractFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.AbstractObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.ManagedBeanDescriptor;
import org.jboss.seam.classic.init.scan.ScannotationScanner;
import org.jboss.seam.classic.runtime.outjection.OutjectedReferenceHolder;
import org.jboss.seam.classic.scope.StatelessContext;
import org.jboss.seam.classic.util.CdiScopeUtils;
import org.jboss.seam.logging.Logger;
import org.jboss.seam.solder.core.Veto;
import org.jboss.seam.solder.reflection.annotated.AnnotatedTypeBuilder;

public class SeamClassicExtension implements Extension {

    private static final Logger log = Logger.getLogger(SeamClassicExtension.class);

    private Set<ManagedBeanDescriptor> beanDescriptors = new HashSet<ManagedBeanDescriptor>();
    private final Set<AbstractFactoryDescriptor> factoryDescriptors = new HashSet<AbstractFactoryDescriptor>();
    private final Set<AbstractObserverMethodDescriptor> observerMethodDescriptors = new HashSet<AbstractObserverMethodDescriptor>();
    private final Set<ComponentsDotXml> configurationFiles = new HashSet<ComponentsDotXml>();
    
    private final StatelessContext statelessContext = new StatelessContext();

    private ClassicBeanTransformer beanTransformer = new ClassicBeanTransformer();

    void init(@Observes BeforeBeanDiscovery event, BeanManager manager) {
        log.debug("Scanning for Seam 2 beans.");
        ScannotationScanner scanner = new ScannotationScanner(this.getClass().getClassLoader());
        scanner.scan();
        Set<Class<?>> classes = scanner.getClasses(Name.class.getName());
        log.debugv("Scan finished. {0} classes were loaded.", classes.size());

        for (Class<?> clazz : classes) {
            ManagedBeanDescriptor beanDescriptor = new ManagedBeanDescriptor(clazz);
            beanDescriptors.add(beanDescriptor);
            factoryDescriptors.addAll(beanDescriptor.getFactories());
        }

        // TODO refactor / move
        try {
            Enumeration<URL> urls = this.getClass().getClassLoader().getResources("META-INF/components.xml");
            while (urls.hasMoreElements()) {
                URL resource = urls.nextElement();
                configurationFiles.add(new ComponentsDotXml(resource.openStream()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ComponentsDotXml configurationFile : configurationFiles) {
            factoryDescriptors.addAll(configurationFile.getFactories());
            observerMethodDescriptors.addAll(configurationFile.getObserverMethods());
        }
        // if (components == null)
        // {
        // log.info("No components.xml");
        // }
        // else
        // {
        // log.info("components.xml found");
        // }

        // TODO: process XML configuration

        // TODO: reduce allDescriptors by processing @Install
        ConditionalInstallationService installationService = new ConditionalInstallationService(beanDescriptors);
        installationService.filterInstallableComponents();
        beanDescriptors = installationService.getInstallableManagedBeanBescriptors();
        factoryDescriptors.addAll(installationService.getInstallableFactoryDescriptors());
        observerMethodDescriptors.addAll(installationService.getInstallableObserverMethodDescriptors());

        beanTransformer.processLegacyBeans(beanDescriptors, manager);
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
    
    void registerScopes(@Observes AfterBeanDiscovery event, BeanManager manager)
    {
        event.addContext(statelessContext);
    }

    void registerFactories(@Observes AfterBeanDiscovery event, BeanManager manager) {
        Set<Bean<?>> factories = beanTransformer.processLegacyFactories(factoryDescriptors, manager);
        log.debugv("Registering {0} factories.", factories.size());
        for (Bean<?> factory : factories) {
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
        Set<ObserverMethod<?>> observerMethods = beanTransformer.processLegacyObserverMethods(observerMethodDescriptors,
                manager);
        log.debugv("Registering {0} observer methods.", observerMethods.size());
        for (ObserverMethod<?> observerMethod : observerMethods) {
            log.debugv("Registering {0}", observerMethod);
            event.addObserverMethod(observerMethod);
        }
    }
    
    void shutdownStatelessContext(@Observes BeforeShutdown event)
    {
        statelessContext.destroyInstances();
    }

    public Set<ManagedBeanDescriptor> getBeanDescriptors() {
        return beanDescriptors;
    }

    public Set<AbstractFactoryDescriptor> getFactoryDescriptors() {
        return factoryDescriptors;
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
}
