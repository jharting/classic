package org.jboss.seam.classic.init.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

import org.jboss.seam.ScopeType;
import org.jboss.seam.classic.util.ClassicScopeUtils;

public class FactoryDescriptor extends AbstractManagedInstanceDescriptor {

    private final String name;
    private final ScopeType specifiedScope;
    private ManagedBeanDescriptor bean;
    private Method method;
    private Class<?> productType;

    public FactoryDescriptor(String name, ScopeType specifiedScope, boolean autoCreate, ManagedBeanDescriptor bean,
            Method method) {
        super(autoCreate);
        this.name = name;
        this.specifiedScope = specifiedScope;
        this.bean = bean;
        this.method = method;
        this.productType = method.getReturnType();
    }

    public ManagedBeanDescriptor getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }

    public ScopeType getSpecifiedScope() {
        return specifiedScope;
    }

    /**
     * Translates Seam 2 ScopeType to matching CDI scope. Default scope rules for Seam 2 factories are considered.
     */
    public Class<? extends Annotation> getCdiScope() {
        if (specifiedScope.equals(ScopeType.UNSPECIFIED)) {
            Class<? extends Annotation> hostScope = bean.getImplicitRole().getCdiScope();
            if (hostScope.equals(Dependent.class)) {
                return RequestScoped.class;
            }
            return hostScope;
        } else {
            return ClassicScopeUtils.transformExplicitLegacyScopeToCdiScope(specifiedScope);
        }
    }

    public Class<?> getProductType() {
        return productType;
    }

    public boolean isVoid() {
        return void.class.equals(productType);
    }

    @Override
    public String toString() {
        return "FactoryDescriptor [name=" + name + ", specifiedScope=" + specifiedScope + ", bean=" + bean + ", method="
                + method + "]";
    }
}
