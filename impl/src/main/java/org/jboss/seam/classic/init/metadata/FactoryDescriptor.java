package org.jboss.seam.classic.init.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

import org.jboss.seam.ScopeType;
import org.jboss.seam.classic.scope.StatelessScoped;
import org.jboss.seam.classic.util.ClassicScopeUtils;
import org.jboss.seam.solder.reflection.Reflections;

public class FactoryDescriptor extends AbstractFactoryDescriptor {

    private ManagedBeanDescriptor bean;
    private Method method;
    private Class<?> productType;

    public FactoryDescriptor(String name, ScopeType specifiedScope, boolean autoCreate, ManagedBeanDescriptor bean,
            Method method) {
        super(name, specifiedScope, autoCreate);
        this.bean = bean;
        this.method = method;
        this.productType = method.getReturnType();
        Reflections.setAccessible(method);
    }

    public FactoryDescriptor(FactoryDescriptor original, ManagedBeanDescriptor bean) {
        this(original.getName(), original.getScope(), original.isAutoCreate(), bean, original.getMethod());
    }

    public ManagedBeanDescriptor getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    /**
     * Translates Seam 2 ScopeType to matching CDI scope. Default scope rules for Seam 2 factories are considered.
     */
    public Class<? extends Annotation> getCdiScope() {
        if (isVoid()) {
            return StatelessScoped.class;
        }
        if (getScope().equals(ScopeType.UNSPECIFIED)) {
            Class<? extends Annotation> hostScope = bean.getImplicitRole().getCdiScope();
            if (hostScope.equals(Dependent.class)) {
                return RequestScoped.class;
            }
            return hostScope;
        } else {
            return ClassicScopeUtils.transformExplicitLegacyScopeToCdiScope(getScope());
        }
    }

    public Class<?> getProductType() {
        return productType;
    }

    public boolean isVoid() {
        return void.class.equals(productType);
    }
}
