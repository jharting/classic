package org.jboss.seam.classic.init.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

public class ManagedBeanDescriptor extends AbstractManagedInstanceDescriptor {

    private final Class<?> javaClass;
    private final InstallDescriptor install;
    // roles
    private final Set<RoleDescriptor> roles = new HashSet<RoleDescriptor>();
    private final RoleDescriptor implicitRole;

    private final Set<FactoryDescriptor> factories = new HashSet<FactoryDescriptor>();
    private final Set<InjectionPointDescriptor> injectionPoints = new HashSet<InjectionPointDescriptor>();
    private final Set<OutjectionPointDescriptor> outjectionPoints = new HashSet<OutjectionPointDescriptor>();
    private final Set<ObserverMethodDescriptor> observerMethods = new HashSet<ObserverMethodDescriptor>();
    private final Method unwrappingMethod;

    public ManagedBeanDescriptor(Class<?> javaClass) {
        super(javaClass);

        if (!javaClass.isAnnotationPresent(Name.class)) {
            throw new IllegalArgumentException(javaClass.getName() + " is not a legacy bean.");
        }

        if (javaClass.isAnnotationPresent(Install.class)) {
            install = new InstallDescriptor(javaClass.getAnnotation(Install.class));
        } else {
            install = new InstallDescriptor();
        }

        this.javaClass = javaClass;

        // Register the implicit role - @Name + @Scope
        String implicitRoleName = javaClass.getAnnotation(Name.class).value();
        ScopeType implicitRoleScope = ScopeType.UNSPECIFIED;
        if (javaClass.isAnnotationPresent(Scope.class)) {
            implicitRoleScope = javaClass.getAnnotation(Scope.class).value();
        }
        implicitRole = new RoleDescriptor(implicitRoleName, implicitRoleScope, this);
        roles.add(implicitRole);

        // Register @Role if present
        if (javaClass.isAnnotationPresent(Role.class)) {
            Role role = javaClass.getAnnotation(Role.class);
            roles.add(new RoleDescriptor(role.name(), role.scope(), this));
        }

        // Register @Roles
        if (javaClass.isAnnotationPresent(Roles.class)) {
            Roles roles = javaClass.getAnnotation(Roles.class);
            for (Role role : roles.value()) {
                this.roles.add(new RoleDescriptor(role.name(), role.scope(), this));
            }
        }

        Method unwrappingMethod = null; // bypassing the final field check
        
        // Iterate over methods
        for (Method method : javaClass.getDeclaredMethods()) {
            // Register @Factory
            if (method.isAnnotationPresent(Factory.class)) {
                Factory factory = method.getAnnotation(Factory.class);
                factories.add(new FactoryDescriptor(factory.value(), factory.scope(), factory.autoCreate(), this, method));
            }
            if (method.isAnnotationPresent(Observer.class)) {
                Observer observer = method.getAnnotation(Observer.class);
                for (String type : observer.value()) {
                    observerMethods.add(new ObserverMethodDescriptor(type, this, method, observer.create()));
                }
            }
            if (method.isAnnotationPresent(Unwrap.class))
            {
                if (unwrappingMethod != null)
                {
                    throw new IllegalStateException("component has multiple @Unwrap methods: " + javaClass.getName());
                }
                unwrappingMethod = method;
            }
        }
        this.unwrappingMethod = unwrappingMethod;

        // @Register @In
        for (Field field : javaClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(In.class)) {
                In in = field.getAnnotation(In.class);
                injectionPoints.add(new InjectionPointDescriptor(in, field, this));
            }
        }

        // Register @Out
        for (Field field : javaClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Out.class)) {
                Out out = field.getAnnotation(Out.class);
                outjectionPoints.add(new OutjectionPointDescriptor(out, field, this));
            }
        }
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public Set<RoleDescriptor> getRoles() {
        return roles;
    }

    public RoleDescriptor getImplicitRole() {
        return implicitRole;
    }

    public Set<FactoryDescriptor> getFactories() {
        return factories;
    }

    public Set<InjectionPointDescriptor> getInjectionPoints() {
        return injectionPoints;
    }

    public Set<OutjectionPointDescriptor> getOutjectionPoints() {
        return outjectionPoints;
    }

    public InstallDescriptor getInstallDescriptor() {
        return install;
    }

    public Set<ObserverMethodDescriptor> getObserverMethods() {
        return observerMethods;
    }
    
    public Method getUnwrappingMethod() {
        return unwrappingMethod;
    }
    
    public boolean hasUnwrappingMethod()
    {
        return unwrappingMethod != null;
    }

    @Override
    public String toString() {
        return "ManagedBeanDescriptor [javaClass=" + javaClass + ", implicitRole=" + implicitRole + "]";
    }
}
