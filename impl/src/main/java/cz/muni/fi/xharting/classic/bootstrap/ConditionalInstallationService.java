package cz.muni.fi.xharting.classic.bootstrap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.solder.reflection.Reflections;
import org.jboss.solder.util.Sortable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cz.muni.fi.xharting.classic.metadata.AbstractFactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.AbstractObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.ElFactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.ElObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.FactoryDescriptor;
import cz.muni.fi.xharting.classic.metadata.BeanDescriptor;
import cz.muni.fi.xharting.classic.metadata.ObserverMethodDescriptor;
import cz.muni.fi.xharting.classic.metadata.RoleDescriptor;

/**
 * Filter for the {@link Install} conditional installation. Beans that are either explicitly disabled or their dependencies are
 * not met are filtered out from further processing. Moreover, bean precedence is taken into account if more than one bean
 * exists for a given name.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ConditionalInstallationService {

    private Comparator<Sortable> comparator = new Sortable.Comparator();

    // lookup maps
    private final Multimap<String, BeanDescriptor> descriptors;
    private Map<String, BeanDescriptor> currentTest;
    // configured factories - these are always installed, so these can satisfy @Install dependencies of other beans
    private Map<String, ElFactoryDescriptor> configuredFactories = new HashMap<String, ElFactoryDescriptor>();
    // installable stuff
    private final Map<String, BeanDescriptor> installableComponents = new HashMap<String, BeanDescriptor>();
    private final Set<AbstractFactoryDescriptor> factories = new HashSet<AbstractFactoryDescriptor>();
    private final Set<AbstractObserverMethodDescriptor> observerMethods = new HashSet<AbstractObserverMethodDescriptor>();

    public ConditionalInstallationService(Collection<BeanDescriptor> incommingDescriptors,
            Set<ElFactoryDescriptor> configuredFactories, Set<ElObserverMethodDescriptor> configuredObserverMethods) {
        this.descriptors = createLookupMap(incommingDescriptors);
        for (ElFactoryDescriptor descriptor : configuredFactories) {
            this.configuredFactories.put(descriptor.getName(), descriptor);
        }
        for (ElObserverMethodDescriptor observerMethodDescriptor : configuredObserverMethods) {
            observerMethods.add(observerMethodDescriptor);
        }
    }

    public ConditionalInstallationService(Collection<BeanDescriptor> incommingDescriptors) {
        this(incommingDescriptors, new HashSet<ElFactoryDescriptor>(), new HashSet<ElObserverMethodDescriptor>());
    }

    public void filterInstallableComponents() {
        for (String component : descriptors.keySet()) {
            currentTest = new HashMap<String, BeanDescriptor>();
            if (installComponent(component)) {
                mergeDescriptorMaps(installableComponents, currentTest);
            }
            currentTest = null;
        }
        for (Map.Entry<String, BeanDescriptor> entry : installableComponents.entrySet()) {
            for (FactoryDescriptor factory : entry.getValue().getFactories()) {
                factories.add(factory);
            }
            for (ElFactoryDescriptor factory : configuredFactories.values()) {
                factories.add(factory);
            }
            for (ObserverMethodDescriptor observerMethod : entry.getValue().getObserverMethods()) {
                observerMethods.add(observerMethod);
            }
        }
    }

    public Set<BeanDescriptor> getInstallableManagedBeanBescriptors() {
        return new HashSet<BeanDescriptor>(installableComponents.values());
    }

    // for tests
    public Map<String, BeanDescriptor> getInstallableManagedBeanDescriptorMap() {
        return Collections.unmodifiableMap(installableComponents);
    }

    public Set<AbstractFactoryDescriptor> getInstallableFactoryDescriptors() {
        return factories;
    }

    public Set<AbstractObserverMethodDescriptor> getInstallableObserverMethodDescriptors() {
        return observerMethods;
    }

    public boolean installComponent(String name) {
        if (isInstalled(name)) {
            return true;
        }
        // sort possible candidates based on precedence
        List<BeanDescriptor> implementations = new ArrayList<BeanDescriptor>(descriptors.get(name));
        Collections.sort(implementations, comparator);
        for (BeanDescriptor implementation : implementations) {

            Map<String, BeanDescriptor> backup = new HashMap<String, BeanDescriptor>(currentTest);
            if (installImplementation(name, implementation)) {
                return true;
            } else {
                // revert
                currentTest = backup;
            }
        }
        return false;
    }

    /**
     * Register a bean.
     * 
     * @return true if all bean's dependencies (including transitive) have been met.
     */
    private boolean installImplementation(String name, BeanDescriptor descriptor) {
        if (!descriptor.getInstallDescriptor().isInstalled()) {
            return false;
        }
        currentTest.put(name, descriptor);
        return checkDependencies(descriptor) && checkClassDependencies(descriptor) && checkGenericDependencies(descriptor);
    }

    /**
     * returns true if and only if all dependencies of a given managed bean are met
     */
    private boolean checkDependencies(BeanDescriptor descriptor) {
        for (String dependency : descriptor.getInstallDescriptor().getDependencies()) {
            if (!installComponent(dependency)) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns true if and only if all class dependencies of a given managed bean are met
     */
    private boolean checkClassDependencies(BeanDescriptor descriptor) {
        for (String dependency : descriptor.getInstallDescriptor().getClassDependencies()) {
            try {
                Reflections.classForName(dependency);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns true if and only if all generic dependencies of a given managed bean are met
     */
    private boolean checkGenericDependencies(BeanDescriptor descriptor) {
        for (Class<?> dependency : descriptor.getInstallDescriptor().getGenericDependencies()) {
            if (!isInstalled(dependency)) {
                Set<BeanDescriptor> candidates = findPotentialComponents(dependency);

                for (BeanDescriptor candidate : candidates) {
                    installComponent(candidate.getImplicitRole().getName());
                }
                if (!isInstalled(dependency)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Based on a collection passed as a parameter, create a bean name -> bean descriptor map
     */
    private Multimap<String, BeanDescriptor> createLookupMap(Collection<BeanDescriptor> incommingDescriptors) {
        Multimap<String, BeanDescriptor> descriptors = HashMultimap.create();
        for (BeanDescriptor descriptor : incommingDescriptors) {
            for (RoleDescriptor role : descriptor.getRoles()) {
                descriptors.put(role.getName(), descriptor);
            }
            for (FactoryDescriptor factory : descriptor.getFactories()) {
                descriptors.put(factory.getName(), descriptor);
            }
        }
        return descriptors;
    }

    /**
     * Populates the map passed as the first parameter with entries from the map passed as the second parameter.
     * 
     * @throws IllegalStateException if the first-parameter map already contains a key from the second-parameter map
     */
    private Map<String, BeanDescriptor> mergeDescriptorMaps(Map<String, BeanDescriptor> map1,
            Map<String, BeanDescriptor> map2) {
        for (Map.Entry<String, BeanDescriptor> entry : map2.entrySet()) {
            if (map1.containsKey(entry.getKey())) {
                throw new IllegalStateException("Map already contains key " + entry.getKey());
            }
            map1.put(entry.getKey(), entry.getValue());
        }
        return map1;
    }

    /**
     * @return true if and only if a bean with the given name is registered
     */
    public boolean isInstalled(String name) {
        return installableComponents.containsKey(name) || currentTest.containsKey(name);
    }

    /**
     * @return true if and only if a bean defined by the class passed as a parameter is registered.
     */
    public boolean isInstalled(Class<?> clazz) {
        for (BeanDescriptor descriptor : installableComponents.values()) {
            if (descriptor.getJavaClass().equals(clazz)) {
                return true;
            }
        }
        for (BeanDescriptor descriptor : currentTest.values()) {
            if (descriptor.getJavaClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A set of {@link BeanDescriptor}s defined by the classes passed as a parameter.
     */
    private Set<BeanDescriptor> findPotentialComponents(Class<?> dependency) {
        Set<BeanDescriptor> candidates = new HashSet<BeanDescriptor>();

        for (BeanDescriptor descriptor : descriptors.values()) {
            if (descriptor.getJavaClass().equals(dependency)) {
                candidates.add(descriptor);
            }
        }
        return candidates;
    }
}
