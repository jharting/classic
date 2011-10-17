package org.jboss.seam.classic.init.metadata;

import java.lang.reflect.Field;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.solder.reflection.Reflections;

public class InjectionPointDescriptor extends AbstractManagedFieldDescriptor {

    private final boolean create;

    public InjectionPointDescriptor(In in, Field field, ManagedBeanDescriptor bean) {
        super(in.value(), in.required(), in.scope(), field, bean);
        this.create = in.create();
    }

    public InjectionPointDescriptor(InjectionPointDescriptor original, ManagedBeanDescriptor bean) {
        this(original.getSpecifiedName(), original.isRequired(), original.getSpecifiedScope(), original.getField(), bean,
                original.isCreate());
    }

    public InjectionPointDescriptor(String name, boolean required, ScopeType specifiedScope, Field field,
            ManagedBeanDescriptor bean, boolean create) {
        super(name, required, specifiedScope, field, bean);
        this.create = create;
    }

    public boolean isCreate() {
        return create;
    }

    public void set(Object target, Object value) {
        Reflections.setFieldValue(false, getField(), target, value);
    }
}
