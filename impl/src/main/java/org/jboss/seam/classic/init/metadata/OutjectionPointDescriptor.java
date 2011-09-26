package org.jboss.seam.classic.init.metadata;

import java.lang.reflect.Field;

import org.jboss.seam.ScopeType;

public class OutjectionPointDescriptor {

    private BeanDescriptor descriptor;
    private String name;
    private boolean required;
    private ScopeType scope;
    private Field field;

    public OutjectionPointDescriptor(BeanDescriptor descriptor, String name, boolean required, ScopeType scope, Field field) {
        this.descriptor = descriptor;
        this.name = name;
        this.required = required;
        this.scope = scope;
        this.field = field;
    }

    public BeanDescriptor getDescriptor() {
        return descriptor;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public ScopeType getScope() {
        return scope;
    }

    public Field getField() {
        return field;
    }

    @Override
    public String toString() {
        return "OutjectionPointDescriptor [descriptor=" + descriptor + ", name=" + name + ", required=" + required + ", scope="
                + scope + ", field=" + field + "]";
    }

}
