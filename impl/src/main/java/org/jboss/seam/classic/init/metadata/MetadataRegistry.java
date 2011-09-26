package org.jboss.seam.classic.init.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.seam.classic.init.SeamClassicExtension;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

@ApplicationScoped
public class MetadataRegistry {

    private Multimap<Class<?>, ManagedBeanDescriptor> managedInstancesByClass = HashMultimap.create();
    private Map<String, AbstractManagedInstanceDescriptor> managedInstancesByName = new HashMap<String, AbstractManagedInstanceDescriptor>();
    
    public MetadataRegistry() {
    }

    @Inject
    public MetadataRegistry(SeamClassicExtension extension)
    {
        // prepare metadata registry for easy lookup at runtime
        for (ManagedBeanDescriptor descriptor : extension.getDescriptors()) {
            managedInstancesByClass.put(descriptor.getJavaClass(), descriptor);
            for (RoleDescriptor role : descriptor.getRoles()) {
                managedInstancesByName.put(role.getName(), descriptor);
            }
            for (FactoryDescriptor factory : descriptor.getFactories())
            {
                if (!factory.isVoid())
                {
                    managedInstancesByName.put(factory.getName(), factory);
                }
            }
        }
    }
    
    public AbstractManagedInstanceDescriptor getManagedInstanceDescriptorByName(String name)
    {
        return managedInstancesByName.get(name);
    }
    
    public Collection<ManagedBeanDescriptor> getManagedInstanceDescriptorByClass(Class<?> clazz, boolean superClassFallback)
    {
        Collection<ManagedBeanDescriptor> descriptors = managedInstancesByClass.get(clazz);
        if (descriptors.isEmpty() && superClassFallback) {
            // let's try again with superclass since the original class might have been a subclass which CDI implementation
            // uses to implement interceptors and decorators
            descriptors = managedInstancesByClass.get(clazz.getSuperclass());
        }
        if (descriptors.isEmpty()) {
            throw new IllegalStateException(clazz + " is not a Seam bean.");
        }
        return descriptors;
    }
    
}
