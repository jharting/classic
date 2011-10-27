package org.jboss.seam.classic.init.metadata;

import java.lang.annotation.Annotation;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.persistence.Entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.classic.util.Seam2Utils;

public class RoleDescriptor {

    private String name;
    private ScopeType specifiedScope;
    private ManagedBeanDescriptor bean;

    public RoleDescriptor(String name, ScopeType specifiedScope, ManagedBeanDescriptor bean) {
        this.name = name;
        this.specifiedScope = specifiedScope;
        this.bean = bean;
    }

    public RoleDescriptor(RoleDescriptor role, ManagedBeanDescriptor managedBeanDescriptor) {
        this(role.getName(), role.getSpecifiedScope(), managedBeanDescriptor);
    }

    public ManagedBeanDescriptor getBean() {
        return bean;
    }

    public void setBean(ManagedBeanDescriptor bean) {
        this.bean = bean;
    }

    public String getName() {
        return name;
    }

    public ScopeType getSpecifiedScope() {
        return specifiedScope;
    }
    
    /**
     * Translates Seam 2 ScopeType to matching CDI scope. Default scope rules for Seam 2 beans are considered.
     */
    public Class<? extends Annotation> getCdiScope() {
        if (specifiedScope.equals(ScopeType.UNSPECIFIED)) {
            Class<?> clazz = bean.getJavaClass();
            if (clazz.isAnnotationPresent(Stateful.class) || clazz.isAnnotationPresent(Entity.class)) {
                return ConversationScoped.class;
            } else if (clazz.isAnnotationPresent(Stateless.class)) {
                return Dependent.class;
            } else {
                return RequestScoped.class;
            }
        } else {
            return Seam2Utils.transformExplicitLegacyScopeToCdiScope(specifiedScope);
        }
    }

    public String toString() {
        return "RoleDescriptor [name=" + name + ", scope=" + specifiedScope + "]";
    }
}
