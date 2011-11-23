package cz.muni.fi.xharting.classic.metadata;

import java.lang.reflect.Field;

import org.jboss.seam.ScopeType;
import org.jboss.solder.reflection.Reflections;

public abstract class AbstractManagedFieldDescriptor {

    private final String specifiedName;
    private final boolean required;
    private final ScopeType specifiedScope;
    private final Field field;
    private final BeanDescriptor bean;

    public AbstractManagedFieldDescriptor(String specifiedName, boolean required, ScopeType specifiedScope, Field field,
            BeanDescriptor bean) {
        this.specifiedName = specifiedName;
        this.required = required;
        this.specifiedScope = specifiedScope;
        this.field = field;
        this.bean = bean;
        Reflections.setAccessible(field);
    }

    public String getSpecifiedName() {
        return specifiedName;
    }

    public String getName() {
        if (specifiedName.isEmpty()) {
            return field.getName();
        }
        return specifiedName;
    }

    public boolean isRequired() {
        return required;
    }

    public ScopeType getSpecifiedScope() {
        return specifiedScope;
    }

    public Field getField() {
        return field;
    }

    public BeanDescriptor getBean() {
        return bean;
    }

    public String getPath() {
        return bean.getJavaClass().getName() + "." + field.getName();
    }

    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public String toString() {
        return "AbstractManagedFieldDescriptor [name=" + getName() + ", required=" + required + ", specifiedScope="
                + specifiedScope + ", field=" + field + "]";
    }

}
