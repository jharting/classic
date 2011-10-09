package org.jboss.seam.classic.init.metadata;

import org.jboss.seam.annotations.Install;

public class InstallDescriptor {

    private boolean installed;
    private String[] dependencies;
    private Class<?>[] genericDependencies;
    private String[] classDependencies;
    private int precedence;

    public InstallDescriptor(Install install) {
        this.installed = install.value();
        this.dependencies = install.dependencies();
        this.genericDependencies = install.genericDependencies();
        this.classDependencies = install.classDependencies();
        this.precedence = install.precedence();
    }

    public InstallDescriptor() {
        this.installed = true;
        this.dependencies = new String[0];
        this.genericDependencies = new Class<?>[0];
        this.classDependencies = new String[0];
        this.precedence = Install.APPLICATION;
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
