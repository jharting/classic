package org.jboss.seam.classic.init.metadata;

import java.lang.reflect.Method;

import org.jboss.seam.ScopeType;

public class FactoryDescriptor {

    private String name;
    private ScopeType scope;
    private boolean autoCreate;
    private BeanDescriptor bean;
    private Method method;

    public FactoryDescriptor(String name, ScopeType scope, boolean autoCreate, BeanDescriptor bean, Method method) {
        this.name = name;
        this.scope = scope;
        this.autoCreate = autoCreate;
        this.bean = bean;
        this.method = method;
    }

    public BeanDescriptor getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public ScopeType getScope() {
        return scope;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }
}
