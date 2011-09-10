package org.jboss.seam.classic.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.seam.classic.init.SeamClassicExtension;
import org.jboss.seam.classic.init.metadata.BeanDescriptor;
import org.jboss.seam.classic.init.metadata.RoleDescriptor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

@ApplicationScoped
public class MetadataRegistry {

    private Multimap<Class<?>, BeanDescriptor> descriptorsByClass = HashMultimap.create();
    private Map<String, BeanDescriptor> descriptorsByName = new HashMap<String, BeanDescriptor>();
    
    public MetadataRegistry() {
    }

    @Inject
    public MetadataRegistry(SeamClassicExtension extension)
    {
        // prepare metadata registry for easy lookup at runtime
        for (BeanDescriptor descriptor : extension.getDescriptors()) {
            descriptorsByClass.put(descriptor.getJavaClass(), descriptor);
            for (RoleDescriptor role : descriptor.getRoles()) {
                descriptorsByName.put(role.getName(), descriptor);
            }
        }
    }
    
    public BeanDescriptor getBeanDescriptorByName(String name)
    {
        return descriptorsByName.get(name);
    }
    
    public Collection<BeanDescriptor> getBeanDescriptorsByClass(Class<?> clazz, boolean superClassFallback)
    {
        Collection<BeanDescriptor> descriptors = descriptorsByClass.get(clazz);
        if (descriptors.isEmpty() && superClassFallback) {
            // let's try again with superclass since the original class might have been a subclass which CDI implementation
            // uses to implement interceptors and decorators
            descriptors = descriptorsByClass.get(clazz.getSuperclass());
        }
        if (descriptors.isEmpty()) {
            throw new IllegalStateException(clazz + " is not a Seam bean.");
        }
        return descriptors;
    }
    
}
