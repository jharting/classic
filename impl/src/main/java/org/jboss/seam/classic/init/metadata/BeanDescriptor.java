package org.jboss.seam.classic.init.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.jboss.seam.annotations.Scope;

public class BeanDescriptor implements ManagedInstanceDescriptor{

    private Class<?> javaClass;
    private boolean autoCreate;
    private Set<RoleDescriptor> roles = new HashSet<RoleDescriptor>();
    private Set<FactoryDescriptor> factories = new HashSet<FactoryDescriptor>();
    private Set<InjectionPointDescriptor> injectionPoints = new HashSet<InjectionPointDescriptor>();
    private Set<OutjectionPointDescriptor> outjectionPoints = new HashSet<OutjectionPointDescriptor>();

    public BeanDescriptor(Class<?> javaClass) {
        if (!javaClass.isAnnotationPresent(Name.class)) {
            throw new IllegalArgumentException(javaClass.getName() + " is not a legacy bean.");
        }

        this.javaClass = javaClass;

        // Register the implicit role - @Name + @Scope
        String implicitRoleName = javaClass.getAnnotation(Name.class).value();
        ScopeType implicitRoleScope = ScopeType.UNSPECIFIED;
        if (javaClass.isAnnotationPresent(Scope.class)) {
            implicitRoleScope = javaClass.getAnnotation(Scope.class).value();
        }
        roles.add(new RoleDescriptor(implicitRoleName, implicitRoleScope));

        // Register @Role if present
        if (javaClass.isAnnotationPresent(Role.class)) {
            Role role = javaClass.getAnnotation(Role.class);
            roles.add(new RoleDescriptor(role.name(), role.scope()));
        }

        // Register @Roles
        if (javaClass.isAnnotationPresent(Roles.class)) {
            Roles roles = javaClass.getAnnotation(Roles.class);
            for (Role role : roles.value()) {
                this.roles.add(new RoleDescriptor(role.name(), role.scope()));
            }
        }

        // Register @AutoCreate
        autoCreate = javaClass.isAnnotationPresent(AutoCreate.class)
                || (javaClass.getPackage() != null && javaClass.getPackage().isAnnotationPresent(AutoCreate.class));

        // Register @Factory
        for (Method method : javaClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Factory.class)) {
                Factory factory = method.getAnnotation(Factory.class);
                if (method.getReturnType().equals(void.class)) // void @Factory
                {
                    Field field = findFieldAssociatedWithFactoryMethod(javaClass, factory.value());
                    if (field == null) {
                        throw new RuntimeException(); // TODO replace with something meaningful
                    }
                    Out out = field.getAnnotation(Out.class);
                    factories
                            .add(new FactoryDescriptor(factory.value(), out.scope(), factory.autoCreate(), this, method, field));
                } else // regular @Factory
                {
                    factories.add(new FactoryDescriptor(factory.value(), factory.scope(), factory.autoCreate(), this, method));
                }

            }
        }

        // @Register @In
        for (Field field : javaClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(In.class)) {
                In in = field.getAnnotation(In.class);
                String name = in.value();
                if ("".equals(name)) {
                    name = field.getName();
                }
                injectionPoints.add(new InjectionPointDescriptor(this, name, in.create(), in.required(), in.scope(), field));
            }
        }

        // Register @Out
        for (Field field : javaClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Out.class)) {
                Out out = field.getAnnotation(Out.class);
                String name = out.value();
                if ("".equals(name)) {
                    name = field.getName();
                }
                outjectionPoints.add(new OutjectionPointDescriptor(this, name, out.required(), out.scope(), field));
            }
        }
    }

    private Field findFieldAssociatedWithFactoryMethod(Class<?> javaClass, String name) {
        for (Field field : javaClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Out.class)) {
                Out out = field.getAnnotation(Out.class);
                if (out.value().equals(name)) {
                    return field;
                }
                if (out.value().equals("") && field.getName().equals(name)) {
                    return field;
                }

            }
        }
        return null;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public Set<RoleDescriptor> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDescriptor> roles) {
        this.roles = roles;
    }

    public Set<FactoryDescriptor> getFactories() {
        return factories;
    }

    public void setFactories(Set<FactoryDescriptor> factories) {
        this.factories = factories;
    }

    public Set<InjectionPointDescriptor> getInjectionPoints() {
        return injectionPoints;
    }

    public void setInjectionPoints(Set<InjectionPointDescriptor> injectionPoints) {
        this.injectionPoints = injectionPoints;
    }

    public Set<OutjectionPointDescriptor> getOutjectionPoints() {
        return outjectionPoints;
    }

    public void setOutjectionPoints(Set<OutjectionPointDescriptor> outjectionPoints) {
        this.outjectionPoints = outjectionPoints;
    }

    public void setJavaClass(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    @Override
    public String toString() {
        return "BeanDescriptor [javaClass=" + javaClass + ", autoCreate=" + autoCreate + ", roles=" + roles + ", factories="
                + factories + ", injectionPoints=" + injectionPoints + "]";
    }
}
