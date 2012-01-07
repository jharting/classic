package cz.muni.fi.xharting.classic.metadata;

import org.jboss.seam.annotations.Install;

/**
 * Holds information read from the {@link Install} annotation.
 * 
 * @author Jozef Hartinger
 * 
 */
public class InstallDescriptor {

    private final boolean installed;
    private final String[] dependencies;
    private final Class<?>[] genericDependencies;
    private final String[] classDependencies;
    private final int precedence;

    public InstallDescriptor(Class<?> clazz) {
        this(clazz, null, null);
    }

    public InstallDescriptor(Class<?> clazz, Boolean installed, Integer precedence) {
        if (clazz.isAnnotationPresent(Install.class)) {
            Install install = clazz.getAnnotation(Install.class);
            this.installed = (installed != null) ? installed : install.value();
            this.dependencies = install.dependencies();
            this.genericDependencies = install.genericDependencies();
            this.classDependencies = install.classDependencies();
            this.precedence = (precedence != null) ? precedence : install.precedence();
        } else {
            this.installed = (installed != null) ? installed : true;
            this.dependencies = new String[0];
            this.genericDependencies = new Class<?>[0];
            this.classDependencies = new String[0];
            this.precedence = (precedence != null) ? precedence : Install.APPLICATION;
        }
    }

    public InstallDescriptor(InstallDescriptor original, Boolean installed, Integer precedence) {
        this.installed = (installed != null) ? installed : original.isInstalled();
        this.dependencies = new String[0];
        this.genericDependencies = new Class<?>[0];
        this.classDependencies = new String[0];
        this.precedence = (precedence != null) ? precedence : original.getPrecedence();
    }

    public boolean isInstalled() {
        return installed;
    }

    public String[] getDependencies() {
        return dependencies;
    }

    public Class<?>[] getGenericDependencies() {
        return genericDependencies;
    }

    public String[] getClassDependencies() {
        return classDependencies;
    }

    public int getPrecedence() {
        return precedence;
    }
}
