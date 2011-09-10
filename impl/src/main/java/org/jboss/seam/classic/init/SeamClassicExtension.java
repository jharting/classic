package org.jboss.seam.classic.init;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.jboss.seam.classic.init.metadata.RoleDescriptor;
import org.jboss.seam.classic.init.scan.ScannotationScanner;
import org.jboss.seam.logging.Logger;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SeamClassicExtension implements Extension {

    private static final Logger log = Logger.getLogger(SeamClassicExtension.class);

    // TODO move into MetadataRegistry
    private Set<BeanDescriptor> descriptors = new HashSet<BeanDescriptor>();
    private Multimap<Class<?>, BeanDescriptor> descriptorsByClass = HashMultimap.create();
    private Map<String, BeanDescriptor> descriptorsByName = new HashMap<String, BeanDescriptor>();

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

        // TODO: process XML configuration

        // prepare metadata registry for easy lookup at runtime
        this.descriptors = enabledDescriptors;
        for (BeanDescriptor descriptor : enabledDescriptors) {
            descriptorsByClass.put(descriptor.getJavaClass(), descriptor);
            for (RoleDescriptor role : descriptor.getRoles()) {
                descriptorsByName.put(role.getName(), descriptor);
            }
        }

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

    public Collection<BeanDescriptor> getDescriptorsByClass(Class<?> javaClass) {
        return descriptorsByClass.get(javaClass);
    }

    public BeanDescriptor getDescriptor(String name) {
        return descriptorsByName.get(name);
    }
}
