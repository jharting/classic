package cz.muni.fi.xharting.classic.metadata;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.annotations.Namespace;

/**
 * Represents a namespace definition read from the {@link Namespace} annotation.
 * 
 * @author Jozef Hartinger
 * 
 */
public class NamespaceDescriptor {
    private final String namespace;
    private final Set<String> packageNames = new HashSet<String>();
    private final String componentPrefix;

    public NamespaceDescriptor(Namespace namespaceAnnotation, Package pkg) {
        this.namespace = namespaceAnnotation.value();
        this.componentPrefix = namespaceAnnotation.prefix();
        packageNames.add(pkg.getName());
    }

    public String getNamespace() {
        return namespace;
    }

    public String getComponentPrefix() {
        return componentPrefix;
    }

    public void addPackageName(String packageName) {
        packageNames.add(packageName);
    }

    public Set<String> getPackageNames() {
        return packageNames;
    }

    @Override
    public String toString() {
        return "NamespaceDescriptor(" + namespace + ')';
    }
}