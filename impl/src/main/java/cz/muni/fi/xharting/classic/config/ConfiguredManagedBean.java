package cz.muni.fi.xharting.classic.config;

import java.util.Arrays;

import org.jboss.seam.ScopeType;

/**
 * Represents a Seam component configuration defined in the configuration file.
 * 
 * @author Jozef Hartinger
 * 
 */
public class ConfiguredManagedBean {

    private String name;
    private final Boolean installed;
    private ScopeType scope;
    private Boolean startup;
    private String[] startupDependends;
    private Class<?> clazz;
    private String jndiName;
    private Integer precedence;
    private Boolean autoCreate;

    public ConfiguredManagedBean(String name, String installed, String scope, String startup, String startupDependends, Class<?> clazz, String jndiName, String precedence,
            String autoCreate) throws ClassNotFoundException {
        this.name = name;
        this.installed = (installed != null) ? Boolean.valueOf(installed) : null;
        this.scope = (scope != null) ? ScopeType.valueOf(scope.toUpperCase()) : null;
        this.startup = (startup != null) ? Boolean.valueOf(startup) : null;
        this.startupDependends = (startupDependends != null) ? startupDependends.split(" ") : null;
        this.clazz = clazz;
        this.jndiName = jndiName;
        this.precedence = (precedence != null) ? Integer.valueOf(precedence) : null;
        this.autoCreate = (autoCreate != null) ? Boolean.valueOf(autoCreate) : null;
    }

    public String getName() {
        return name;
    }

    public Boolean getInstalled() {
        return installed;
    }

    public ScopeType getScope() {
        return scope;
    }

    public Boolean getStartup() {
        return startup;
    }

    public String[] getStartupDependends() {
        return startupDependends;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getJndiName() {
        return jndiName;
    }

    public Integer getPrecedence() {
        return precedence;
    }

    public Boolean getAutoCreate() {
        return autoCreate;
    }

    @Override
    public String toString() {
        return "ConfiguredManagedBean [name=" + name + ", installed=" + installed + ", scope=" + scope + ", startup=" + startup + ", startupDependends="
                + Arrays.toString(startupDependends) + ", clazz=" + clazz + ", jndiName=" + jndiName + ", precedence=" + precedence + ", autoCreate=" + autoCreate + "]";
    }
}
