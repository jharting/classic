package org.jboss.seam.classic.init.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.classic.util.ClassicScopeUtils;
import org.jboss.solder.reflection.Reflections;

public class OutjectionPointDescriptor extends AbstractManagedFieldDescriptor {

    public OutjectionPointDescriptor(String specifiedName, boolean required, ScopeType specifiedScope, Field field,
            ManagedBeanDescriptor bean) {
        super(specifiedName, required, specifiedScope, field, bean);
        if (specifiedScope == ScopeType.STATELESS) {
            throw new IllegalArgumentException("cannot specify explicit scope=STATELESS on @Out: " + getPath());
        }
    }

    public OutjectionPointDescriptor(Out out, Field field, ManagedBeanDescriptor bean) {
        super(out.value(), out.required(), out.scope(), field, bean);
    }

    public OutjectionPointDescriptor(OutjectionPointDescriptor descriptor, ManagedBeanDescriptor bean) {
        this(descriptor.getSpecifiedName(), descriptor.isRequired(), descriptor.getSpecifiedScope(), descriptor.getField(),
                bean);
    }

    public Object get(Object target) {
        return Reflections.getFieldValue(getField(), target);
    }

    /**
     * Translates Seam 2 ScopeType to matching CDI scope. Default scope rules for Seam 2 outjected fields are considered.
     */
    public Class<? extends Annotation> getCdiScope() {
        if (getSpecifiedScope() == ScopeType.UNSPECIFIED) {
            Class<? extends Annotation> hostScope = getBean().getImplicitRole().getCdiScope();

            if (Dependent.class.equals(hostScope)) {
                return RequestScoped.class;
            }
            return hostScope;
        }
        return ClassicScopeUtils.transformExplicitLegacyScopeToCdiScope(getSpecifiedScope());
    }
}
