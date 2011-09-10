package org.jboss.seam.classic.init.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.seam.ScopeType;

public class FactoryDescriptor {

    private String name;
    private ScopeType scope;
    private boolean autoCreate;
    private BeanDescriptor bean;
    private Method method;
    private Field field;
    private Class<?> productType;

    private FactoryDescriptor(String name, ScopeType scope, boolean autoCreate, BeanDescriptor bean) {
        this.name = name;
        this.scope = scope;
        this.autoCreate = autoCreate;
        this.bean = bean;
    }

    public FactoryDescriptor(String name, ScopeType scope, boolean autoCreate, BeanDescriptor bean, Method method) {
        this(name, scope, autoCreate, bean);
        this.method = method;
        this.productType = method.getReturnType();
    }

    public FactoryDescriptor(String name, ScopeType scope, boolean autoCreate, BeanDescriptor bean, Method method, Field field) {
        this(name, scope, autoCreate, bean);
        if (!method.getReturnType().equals(void.class))
        {
            throw new IllegalArgumentException("Non-void factory method cannot define an outjectable field of the same name.");
        }
        this.method = method;
        this.field = field;
        this.productType = field.getType();
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

    public Field getField() {
        return field;
    }

    public Class<?> getProductType() {
        return productType;
    }
    
    public boolean isVoid()
    {
        return field != null;
    }

    @Override
    public String toString() {
        return "FactoryDescriptor [name=" + name + ", scope=" + scope + ", autoCreate=" + autoCreate + ", bean=" + bean
                + ", method=" + method + ", field=" + field + ", productType=" + productType + "]";
    }
    
    
}
