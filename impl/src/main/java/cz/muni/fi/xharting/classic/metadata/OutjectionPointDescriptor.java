package cz.muni.fi.xharting.classic.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Out;
import org.jboss.solder.reflection.Reflections;

import cz.muni.fi.xharting.classic.util.Seam2Utils;

public class OutjectionPointDescriptor extends AbstractManagedFieldDescriptor {

    public OutjectionPointDescriptor(String specifiedName, boolean required, ScopeType specifiedScope, Field field,
            BeanDescriptor bean) {
        super(specifiedName, required, specifiedScope, field, bean);
        if (specifiedScope == ScopeType.STATELESS) {
            throw new IllegalArgumentException("cannot specify explicit scope=STATELESS on @Out: " + getPath());
        }
    }

    public OutjectionPointDescriptor(Out out, Field field, BeanDescriptor bean) {
        super(out.value(), out.required(), out.scope(), field, bean);
    }

    public OutjectionPointDescriptor(OutjectionPointDescriptor descriptor, BeanDescriptor bean) {
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
        return Seam2Utils.transformExplicitLegacyScopeToCdiScope(getSpecifiedScope());
    }
}
