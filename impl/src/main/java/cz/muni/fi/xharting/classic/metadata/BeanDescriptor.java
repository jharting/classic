package cz.muni.fi.xharting.classic.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.reflection.Reflections;
import org.jboss.solder.util.Sortable;

import cz.muni.fi.xharting.classic.config.ConfiguredManagedBean;

/**
 * Represents a class-based Seam 2 component (managed bean or session bean).
 * 
 * @author Jozef Hartinger
 * 
 */
public class BeanDescriptor extends AbstractManagedInstanceDescriptor implements Sortable {

    public enum BeanType {
        MANAGED_BEAN, STATELESS, STATEFUL, SINGLETON, ENTITY;
    }

    private static final Logger log = Logger.getLogger(BeanDescriptor.class);

    private final Class<?> javaClass;
    private final BeanType beanType;
    private final InstallDescriptor install;

    private final boolean startup;
    private final String[] startupDependencies;

    // roles
    private final Set<RoleDescriptor> roles = new HashSet<RoleDescriptor>();
    private final RoleDescriptor implicitRole;

    private final Set<FactoryDescriptor> factories = new HashSet<FactoryDescriptor>();
    private final Set<InjectionPointDescriptor> injectionPoints = new HashSet<InjectionPointDescriptor>();
    private final Set<OutjectionPointDescriptor> outjectionPoints = new HashSet<OutjectionPointDescriptor>();
    // these are EE-injected fields which we want to decorate
    private final Set<DecoratingInjectionPoint<EntityManager>> persistenceContextFields = new HashSet<DecoratingInjectionPoint<EntityManager>>();
    private final Set<ObserverMethodDescriptor> observerMethods = new HashSet<ObserverMethodDescriptor>();
    private final Method unwrappingMethod;

    public BeanDescriptor(Class<?> javaClass) {
        super(javaClass);

        if (!javaClass.isAnnotationPresent(Name.class)) {
            throw new IllegalArgumentException(javaClass.getName() + " is not a legacy bean.");
        }

        this.javaClass = javaClass;
        this.beanType = determineBeanType();
        install = new InstallDescriptor(javaClass);
        startup = javaClass.isAnnotationPresent(Startup.class);
        startupDependencies = processStartupDependencies(javaClass);


        // Register the implicit role - @Name + @Scope
        String implicitRoleName = javaClass.getAnnotation(Name.class).value();
        ScopeType implicitRoleScope = ScopeType.UNSPECIFIED;
        if (javaClass.isAnnotationPresent(Scope.class)) {
            implicitRoleScope = javaClass.getAnnotation(Scope.class).value();
        }

        this.implicitRole = processRoles(implicitRoleName, implicitRoleScope, javaClass);
        // process methods (@Factory, @Observer, @Unwrap)
        this.unwrappingMethod = processMethods(javaClass);
        processFields(javaClass);

    }

    public BeanDescriptor(ConfiguredManagedBean configuredManagedBean) {
        this(configuredManagedBean, configuredManagedBean.getClazz());
    }

    public BeanDescriptor(ConfiguredManagedBean configuredManagedBean, Class<?> javaClass) {
        super(configuredManagedBean.getAutoCreate(), javaClass);
        this.javaClass = javaClass;
        this.beanType = determineBeanType();
        install = new InstallDescriptor(javaClass, configuredManagedBean.getInstalled(), configuredManagedBean.getPrecedence());
        startup = (configuredManagedBean.getStartup() != null) ? configuredManagedBean.getStartup() : javaClass
                .isAnnotationPresent(Startup.class);
        startupDependencies = (configuredManagedBean.getStartupDependends() != null) ? configuredManagedBean
                .getStartupDependends() : processStartupDependencies(javaClass);

        String implicitRoleName = configuredManagedBean.getName();
        ScopeType implicitRoleScope = ScopeType.UNSPECIFIED;
        if (configuredManagedBean.getScope() != null) {
            implicitRoleScope = configuredManagedBean.getScope();
        } else if (javaClass.isAnnotationPresent(Scope.class)) {
            implicitRoleScope = javaClass.getAnnotation(Scope.class).value();
        }

        this.implicitRole = processRoles(implicitRoleName, implicitRoleScope, javaClass);
        // process methods (@Factory, @Observer, @Unwrap)
        this.unwrappingMethod = processMethods(javaClass);
        processFields(javaClass);
    }

    public BeanDescriptor(ConfiguredManagedBean configuredManagedBean, BeanDescriptor managedBeanDescriptor) {
        super(configuredManagedBean.getAutoCreate(), managedBeanDescriptor.getJavaClass());
        if (configuredManagedBean.getClazz() != null
                && !configuredManagedBean.getClazz().equals(managedBeanDescriptor.getJavaClass())) {
            throw new IllegalStateException("Cannot redefine metadata for a different class");
        }
        this.javaClass = managedBeanDescriptor.getJavaClass();
        this.beanType = determineBeanType();
        this.install = new InstallDescriptor(managedBeanDescriptor.getInstallDescriptor(),
                configuredManagedBean.getInstalled(), configuredManagedBean.getPrecedence());
        startup = (configuredManagedBean.getStartup() != null) ? configuredManagedBean.getStartup() : managedBeanDescriptor
                .isStartup();
        startupDependencies = (configuredManagedBean.getStartupDependends() != null) ? configuredManagedBean
                .getStartupDependends() : managedBeanDescriptor.getStartupDependencies();

        // copy roles
        String implicitRoleName = configuredManagedBean.getName();
        ScopeType implicitRoleScope = managedBeanDescriptor.getImplicitRole().getSpecifiedScope();
        if (configuredManagedBean.getScope() != null) {
            implicitRoleScope = configuredManagedBean.getScope();
        }
        this.implicitRole = new RoleDescriptor(implicitRoleName, implicitRoleScope, this);
        roles.add(implicitRole);

        for (RoleDescriptor role : managedBeanDescriptor.getRoles()) {
            // we already added the implicit scope
            if (!role.equals(managedBeanDescriptor.getImplicitRole())) {
                roles.add(new RoleDescriptor(role, this));
            }
        }

        // copy the rest
        this.unwrappingMethod = managedBeanDescriptor.getUnwrappingMethod();
        for (FactoryDescriptor descriptor : managedBeanDescriptor.getFactories()) {
            factories.add(new FactoryDescriptor(descriptor, this));
        }
        for (ObserverMethodDescriptor observer : managedBeanDescriptor.getObserverMethods()) {
            observerMethods.add(new ObserverMethodDescriptor(observer, this));
        }
        for (InjectionPointDescriptor descriptor : managedBeanDescriptor.getInjectionPoints()) {
            injectionPoints.add(new InjectionPointDescriptor(descriptor, this));
        }
        for (OutjectionPointDescriptor descriptor : managedBeanDescriptor.getOutjectionPoints()) {
            outjectionPoints.add(new OutjectionPointDescriptor(descriptor, this));
        }
    }

    private RoleDescriptor processRoles(String implicitRoleName, ScopeType implicitRoleScope, Class<?> javaClass) {
        RoleDescriptor implicitRole = new RoleDescriptor(implicitRoleName, implicitRoleScope, this);
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
        return implicitRole;
    }

    private Method processMethods(Class<?> javaClass) {
        Method unwrappingMethod = null; // bypassing the final field check
        for (Class<?> clazz = javaClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
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
                if (method.isAnnotationPresent(Unwrap.class)) {
                    if (unwrappingMethod != null) {
                        throw new IllegalStateException("component has multiple @Unwrap methods: " + javaClass.getName());
                    }
                    unwrappingMethod = method;
                    Reflections.setAccessible(unwrappingMethod);
                }
            }
        }
        return unwrappingMethod;
    }

    private void processFields(Class<?> javaClass) {
        for (Class<?> clazz = javaClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(In.class)) {
                    In in = field.getAnnotation(In.class);
                    injectionPoints.add(new InjectionPointDescriptor(in, field, this));
                }
                if (field.isAnnotationPresent(Out.class)) {
                    Out out = field.getAnnotation(Out.class);
                    outjectionPoints.add(new OutjectionPointDescriptor(out, field, this));
                }
                if (field.isAnnotationPresent(DataModel.class) || field.isAnnotationPresent(DataModelSelection.class)
                        || field.isAnnotationPresent(DataModelSelectionIndex.class)) {
                    log.warn("DataModels are not supported. " + field);
                }
                if (field.isAnnotationPresent(PersistenceContext.class) && EntityManager.class.equals(field.getType())) {
                    persistenceContextFields.add(new DecoratingInjectionPoint<EntityManager>(field));
                }
            }
        }
    }

    private String[] processStartupDependencies(Class<?> javaClass) {
        if (javaClass.isAnnotationPresent(Startup.class)) {
            return javaClass.getAnnotation(Startup.class).depends();
        } else {
            return new String[0];
        }
    }

    private BeanType determineBeanType() {
        if (javaClass.isAnnotationPresent(Stateless.class)) {
            return BeanType.STATELESS;
        }
        if (javaClass.isAnnotationPresent(Stateful.class)) {
            return BeanType.STATEFUL;
        }
        if (javaClass.isAnnotationPresent(Singleton.class)) {
            return BeanType.SINGLETON;
        }
        if (javaClass.isAnnotationPresent(Entity.class)) {
            return BeanType.ENTITY;
        }
        return BeanType.MANAGED_BEAN;
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

    public boolean hasUnwrappingMethod() {
        return unwrappingMethod != null;
    }

    public boolean isStartup() {
        return startup;
    }

    public String[] getStartupDependencies() {
        return startupDependencies;
    }

    @Override
    public String toString() {
        return "ManagedBeanDescriptor [javaClass=" + javaClass + ", implicitRole=" + implicitRole + "]";
    }

    @Override
    public int getPrecedence() {
        return install.getPrecedence();
    }

    /**
     * Returns true if and only if the component is define by the {@link Name} annotation - as oposed to being configured in XML
     * with explicit class declaration where the class does not contain {@link Name}.
     * 
     * @return
     */
    public boolean isDefinedByClass() {
        return javaClass.isAnnotationPresent(Name.class);
    }

    public Set<DecoratingInjectionPoint<EntityManager>> getPersistenceContextFields() {
        return persistenceContextFields;
    }

    public BeanType getBeanType() {
        return beanType;
    }
}
