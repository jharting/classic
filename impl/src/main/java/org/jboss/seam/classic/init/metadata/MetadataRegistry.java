package org.jboss.seam.classic.init.metadata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.classic.init.ConditionalInstallationService;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class MetadataRegistry {

    // basics
    private final Set<ManagedBeanDescriptor> managedBeanDescriptors;
    private final Set<AbstractFactoryDescriptor> factoryDescriptors;
    private final Set<AbstractObserverMethodDescriptor> observerMethods;

    // lookup structures
    private final Multimap<Class<?>, ManagedBeanDescriptor> managedInstancesByClass = HashMultimap.create();
    private final Map<String, AbstractManagedInstanceDescriptor> managedInstancesByName = new HashMap<String, AbstractManagedInstanceDescriptor>();

    public MetadataRegistry(ConditionalInstallationService service) {
        this(service.getInstallableManagedBeanBescriptors(), service.getInstallableFactoryDescriptors(), service
                .getInstallableObserverMethodDescriptors());
    }
    
    // make this bean proxyable
    MetadataRegistry() {
        managedBeanDescriptors = null;
        factoryDescriptors = null;
        observerMethods = null;
    }

    public MetadataRegistry(Set<ManagedBeanDescriptor> managedBeanDescriptors,
            Set<AbstractFactoryDescriptor> factoryDescriptors, Set<AbstractObserverMethodDescriptor> observerMethods) {
        this.managedBeanDescriptors = managedBeanDescriptors;
        this.factoryDescriptors = factoryDescriptors;
        this.observerMethods = observerMethods;

        for (ManagedBeanDescriptor descriptor : managedBeanDescriptors) {
            managedInstancesByClass.put(descriptor.getJavaClass(), descriptor);
            for (RoleDescriptor role : descriptor.getRoles()) {
                managedInstancesByName.put(role.getName(), descriptor);
            }
        }
        for (AbstractFactoryDescriptor descriptor : factoryDescriptors) {
            managedInstancesByName.put(descriptor.getName(), descriptor);
        }
    }

    public AbstractManagedInstanceDescriptor getManagedInstanceDescriptorByName(String name) {
        return managedInstancesByName.get(name);
    }

    public Collection<ManagedBeanDescriptor> getManagedInstanceDescriptorByClass(Class<?> clazz, boolean superClassFallback) {
        Collection<ManagedBeanDescriptor> descriptors = managedInstancesByClass.get(clazz);
        if (descriptors.isEmpty() && superClassFallback) {
            // let's try again with superclass since the original class might have been a subclass which CDI implementation
            // uses to implement interceptors and decorators
            descriptors = managedInstancesByClass.get(clazz.getSuperclass());
        }
        if (descriptors.isEmpty()) {
            throw new IllegalStateException(clazz + " is not a Seam bean.");
        }
        return Collections.unmodifiableCollection(descriptors);
    }

    public Set<ManagedBeanDescriptor> getManagedBeanDescriptors() {
        return Collections.unmodifiableSet(managedBeanDescriptors);
    }

    public Set<AbstractFactoryDescriptor> getFactoryDescriptors() {
        return Collections.unmodifiableSet(factoryDescriptors);
    }

    public Set<AbstractObserverMethodDescriptor> getObserverMethods() {
        return Collections.unmodifiableSet(observerMethods);
    }
}
