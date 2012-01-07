package cz.muni.fi.xharting.classic.metadata;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cz.muni.fi.xharting.classic.bootstrap.ConditionalInstallationService;

/**
 * The central point that stores metadata about beans, factories and observer methods. The registry is available for CDI
 * injection.
 * 
 * @author Jozef Hartinger
 * 
 */
public class MetadataRegistry {

    // basics
    private final Set<BeanDescriptor> managedBeanDescriptors;
    private final Set<AbstractFactoryDescriptor> factoryDescriptors;
    private final Set<AbstractObserverMethodDescriptor> observerMethods;

    // lookup structures
    private final Multimap<Class<?>, BeanDescriptor> managedInstancesByClass = HashMultimap.create();
    private final Map<String, AbstractManagedInstanceDescriptor> managedInstancesByName = new HashMap<String, AbstractManagedInstanceDescriptor>();

    private final Multimap<Class<? extends Annotation>, RoleDescriptor> startupBeans = HashMultimap.create();

    public MetadataRegistry(ConditionalInstallationService service) {
        this(service.getInstallableManagedBeanBescriptors(), service.getInstallableFactoryDescriptors(), service.getInstallableObserverMethodDescriptors());
    }

    // make this bean proxyable
    MetadataRegistry() {
        managedBeanDescriptors = null;
        factoryDescriptors = null;
        observerMethods = null;
    }

    public MetadataRegistry(Set<BeanDescriptor> managedBeanDescriptors, Set<AbstractFactoryDescriptor> factoryDescriptors, Set<AbstractObserverMethodDescriptor> observerMethods) {
        this.managedBeanDescriptors = managedBeanDescriptors;
        this.factoryDescriptors = factoryDescriptors;
        this.observerMethods = observerMethods;

        for (BeanDescriptor descriptor : managedBeanDescriptors) {
            managedInstancesByClass.put(descriptor.getJavaClass(), descriptor);
            for (RoleDescriptor role : descriptor.getRoles()) {
                managedInstancesByName.put(role.getName(), descriptor);
                if (descriptor.isStartup()) {
                    Class<? extends Annotation> scope = role.getCdiScope();
                    if (!SessionScoped.class.equals(scope) && !ApplicationScoped.class.equals(scope)) {
                        throw new IllegalArgumentException("@Startup only supported for SESSION or APPLICATION scoped components: " + role.getName());
                    }
                    startupBeans.put(role.getCdiScope(), role);
                }
            }
        }
        for (AbstractFactoryDescriptor descriptor : factoryDescriptors) {
            managedInstancesByName.put(descriptor.getName(), descriptor);
        }
        checkStartupDependenciesForCycles();
    }

    @SuppressWarnings("unchecked")
    protected void checkStartupDependenciesForCycles() {
        for (Class<?> scope : new Class<?>[] { ApplicationScoped.class, SessionScoped.class }) {
            for (RoleDescriptor role : startupBeans.get((Class<? extends Annotation>) scope)) {
                Set<String> pendingStartup = new HashSet<String>();
                checkStartupDependenciesForCycles(role.getName(), pendingStartup);
            }
        }
    }

    protected void checkStartupDependenciesForCycles(String name, Set<String> pendingStartup) {
        if (pendingStartup.contains(name)) {
            throw new IllegalArgumentException("Cyclic startup dependency found: " + pendingStartup);
        } else {
            pendingStartup.add(name);
            BeanDescriptor descriptor = getManagedBeanDescriptorByName(name);
            if (descriptor != null) {
                for (String dependency : descriptor.getStartupDependencies()) {
                    checkStartupDependenciesForCycles(dependency, pendingStartup);
                }
            }
            pendingStartup.remove(name);
        }
    }

    public AbstractManagedInstanceDescriptor getManagedInstanceDescriptorByName(String name) {
        return managedInstancesByName.get(name);
    }

    public BeanDescriptor getManagedBeanDescriptorByName(String name) {
        AbstractManagedInstanceDescriptor descriptor = getManagedInstanceDescriptorByName(name);
        if (descriptor != null && descriptor instanceof BeanDescriptor) {
            return (BeanDescriptor) descriptor;
        } else {
            return null;
        }
    }

    public Collection<BeanDescriptor> getManagedInstanceDescriptorByClass(Class<?> clazz, boolean superClassFallback) {
        Collection<BeanDescriptor> descriptors = managedInstancesByClass.get(clazz);
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

    public Set<BeanDescriptor> getManagedBeanDescriptors() {
        return Collections.unmodifiableSet(managedBeanDescriptors);
    }

    public Set<AbstractFactoryDescriptor> getFactoryDescriptors() {
        return Collections.unmodifiableSet(factoryDescriptors);
    }

    public Set<AbstractObserverMethodDescriptor> getObserverMethods() {
        return Collections.unmodifiableSet(observerMethods);
    }

    public Collection<RoleDescriptor> getStartupBeans(Class<? extends Annotation> scope) {
        return startupBeans.get(scope);
    }
}
