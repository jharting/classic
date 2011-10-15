package org.jboss.seam.classic.init;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.classic.init.metadata.AbstractFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.AbstractObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.ElFactoryDescriptor;
import org.jboss.seam.classic.init.metadata.ElObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.FactoryDescriptor;
import org.jboss.seam.classic.init.metadata.ManagedBeanDescriptor;
import org.jboss.seam.classic.init.metadata.ObserverMethodDescriptor;
import org.jboss.seam.classic.init.metadata.RoleDescriptor;
import org.jboss.seam.solder.reflection.Reflections;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Filter for the {@link Install} conditional installation. Beans that are either explicitly disabled or their dependencies are
 * not met are filtered out from further processing. Moreover, bean precedence is taken into account if more than one bean
 * exists for a given name.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ConditionalInstallationService {
    
    private PrecedenceComparator comparator = new PrecedenceComparator();

    // lookup maps
    private final Multimap<String, ManagedBeanDescriptor> descriptors;
    private Map<String, ManagedBeanDescriptor> currentTest;
    // configured factories - these are always installed, so these can satisfy @Install dependencies of other beans
    private Map<String, ElFactoryDescriptor> configuredFactories = new HashMap<String, ElFactoryDescriptor>();
    // installable stuff
    private final Map<String, ManagedBeanDescriptor> installableComponents = new HashMap<String, ManagedBeanDescriptor>();
    private final Set<AbstractFactoryDescriptor> factories = new HashSet<AbstractFactoryDescriptor>();
    private final Set<AbstractObserverMethodDescriptor> observerMethods = new HashSet<AbstractObserverMethodDescriptor>();

    public ConditionalInstallationService(Collection<ManagedBeanDescriptor> incommingDescriptors, Set<ElFactoryDescriptor> configuredFactories, Set<ElObserverMethodDescriptor> configuredObserverMethods) {
        this.descriptors = createLookupMap(incommingDescriptors);
        for (ElFactoryDescriptor descriptor : configuredFactories)
        {
            this.configuredFactories.put(descriptor.getName(), descriptor);
        }
        for (ElObserverMethodDescriptor observerMethodDescriptor : configuredObserverMethods)
        {
            observerMethods.add(observerMethodDescriptor);
        }
    }
    
    public ConditionalInstallationService(Collection<ManagedBeanDescriptor> incommingDescriptors)
    {
        this(incommingDescriptors, new HashSet<ElFactoryDescriptor>(), new HashSet<ElObserverMethodDescriptor>());
    }

    public void filterInstallableComponents() {
        for (String component : descriptors.keySet()) {
            currentTest = new HashMap<String, ManagedBeanDescriptor>();
            if (installComponent(component)) {
                mergeDescriptorMaps(installableComponents, currentTest);
            }
            currentTest = null;
        }
        for (Map.Entry<String, ManagedBeanDescriptor> entry : installableComponents.entrySet())
        {
            for (FactoryDescriptor factory : entry.getValue().getFactories())
            {
                factories.add(factory);
            }
            for (ElFactoryDescriptor factory : configuredFactories.values())
            {
                factories.add(factory);
            }
            for (ObserverMethodDescriptor observerMethod : entry.getValue().getObserverMethods())
            {
                observerMethods.add(observerMethod);
            }
        }
    }
    
    public Set<ManagedBeanDescriptor> getInstallableManagedBeanBescriptors()
    {
        return new HashSet<ManagedBeanDescriptor>(installableComponents.values());
    }
    
    // for tests
    public Map<String, ManagedBeanDescriptor> getInstallableManagedBeanDescriptorMap()
    {
        return Collections.unmodifiableMap(installableComponents);
    }
    
    public Set<AbstractFactoryDescriptor> getInstallableFactoryDescriptors()
    {
        return factories;
    }
    
    public Set<AbstractObserverMethodDescriptor> getInstallableObserverMethodDescriptors()
    {
        return observerMethods;
    }

    public boolean installComponent(String name) {
        if (isInstalled(name)) {
            return true;
        }
        // sort possible candidates based on precedence
        List<ManagedBeanDescriptor> implementations = new ArrayList<ManagedBeanDescriptor>(descriptors.get(name));
        Collections.sort(implementations, comparator);
        for (ManagedBeanDescriptor implementation : implementations) {

            Map<String, ManagedBeanDescriptor> backup = new HashMap<String, ManagedBeanDescriptor>(currentTest);
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
     * @return true if all bean's dependencies (including transitive) have been met. 
     */
    private boolean installImplementation(String name, ManagedBeanDescriptor descriptor) {
        if (!descriptor.getInstallDescriptor().isInstalled())
        {
            return false;
        }
        currentTest.put(name, descriptor);
        return checkDependencies(descriptor) && checkClassDependencies(descriptor) && checkGenericDependencies(descriptor);
    }

    /**
     * returns true if and only if all dependencies of a given managed bean are met
     */
    private boolean checkDependencies(ManagedBeanDescriptor descriptor) {
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
    private boolean checkClassDependencies(ManagedBeanDescriptor descriptor) {
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
    private boolean checkGenericDependencies(ManagedBeanDescriptor descriptor) {
        for (Class<?> dependency : descriptor.getInstallDescriptor().getGenericDependencies()) {
            if (!isInstalled(dependency)) {
                Set<ManagedBeanDescriptor> candidates = findPotentialComponents(dependency);

                for (ManagedBeanDescriptor candidate : candidates) {
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
    private Multimap<String, ManagedBeanDescriptor> createLookupMap(Collection<ManagedBeanDescriptor> incommingDescriptors) {
        Multimap<String, ManagedBeanDescriptor> descriptors = HashMultimap.create();
        for (ManagedBeanDescriptor descriptor : incommingDescriptors) {
            for (RoleDescriptor role : descriptor.getRoles()) {
                descriptors.put(role.getName(), descriptor);
            }
            for (FactoryDescriptor factory : descriptor.getFactories())
            {
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
    private Map<String, ManagedBeanDescriptor> mergeDescriptorMaps(Map<String, ManagedBeanDescriptor> map1,
            Map<String, ManagedBeanDescriptor> map2) {
        for (Map.Entry<String, ManagedBeanDescriptor> entry : map2.entrySet()) {
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
        for (ManagedBeanDescriptor descriptor : installableComponents.values()) {
            if (descriptor.getJavaClass().equals(clazz)) {
                return true;
            }
        }
        for (ManagedBeanDescriptor descriptor : currentTest.values()) {
            if (descriptor.getJavaClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A set of {@link ManagedBeanDescriptor}s defined by the classes passed as a parameter.
     */
    private Set<ManagedBeanDescriptor> findPotentialComponents(Class<?> dependency) {
        Set<ManagedBeanDescriptor> candidates = new HashSet<ManagedBeanDescriptor>();

        for (ManagedBeanDescriptor descriptor : descriptors.values()) {
            if (descriptor.getJavaClass().equals(dependency)) {
                candidates.add(descriptor);
            }
        }
        return candidates;
    }

    public static class PrecedenceComparator implements Comparator<ManagedBeanDescriptor> {
        public int compare(ManagedBeanDescriptor o1, ManagedBeanDescriptor o2) {
            return o2.getInstallDescriptor().getPrecedence() - o1.getInstallDescriptor().getPrecedence();
        }

    }

}
