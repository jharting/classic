package org.jboss.seam.classic.init.metadata;

import java.lang.reflect.Field;

import org.jboss.seam.ScopeType;

public class InjectionPointDescriptor {

    private BeanDescriptor bean;
    private String name;
    private boolean create;
    private boolean required;
    private ScopeType scope;
    private Field field;

    public InjectionPointDescriptor(BeanDescriptor bean, String name, boolean create, boolean required, ScopeType scope, Field field) {
        this.bean = bean;
        this.name = name;
        this.create = create;
        this.required = required;
        this.scope = scope;
        this.field = field;
    }

    public BeanDescriptor getBean() {
        return bean;
    }

    public String getName() {
        return name;
    }

    public boolean isCreate() {
        return create;
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
}
