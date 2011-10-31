package cz.muni.fi.xharting.classic.metadata;

import java.lang.annotation.Annotation;

import javax.ejb.Stateless;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

import org.jboss.seam.ScopeType;

import cz.muni.fi.xharting.classic.metadata.BeanDescriptor.BeanType;
import cz.muni.fi.xharting.classic.util.Seam2Utils;

public class RoleDescriptor {

    private String name;
    private ScopeType specifiedScope;
    private BeanDescriptor bean;

    public RoleDescriptor(String name, ScopeType specifiedScope, BeanDescriptor bean) {
        this.name = name;
        this.specifiedScope = specifiedScope;
        this.bean = bean;
    }

    public RoleDescriptor(RoleDescriptor role, BeanDescriptor managedBeanDescriptor) {
        this(role.getName(), role.getSpecifiedScope(), managedBeanDescriptor);
    }

    public BeanDescriptor getBean() {
        return bean;
    }

    public void setBean(BeanDescriptor bean) {
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
            if (getBean().getBeanType().equals(BeanType.STATEFUL) || getBean().getBeanType().equals(BeanType.ENTITY)) {
                return ConversationScoped.class;
            } else if (getBean().getBeanType().equals(BeanType.STATELESS)) {
                return Dependent.class;
            } else {
                return RequestScoped.class;
            }
        } else if (bean.getJavaClass().isAnnotationPresent(Stateless.class)) {
            return Dependent.class;
        } else {
            return Seam2Utils.transformExplicitLegacyScopeToCdiScope(specifiedScope);
        }
    }

    public String toString() {
        return "RoleDescriptor [name=" + name + ", scope=" + specifiedScope + "]";
    }
}
