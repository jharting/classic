package cz.muni.fi.xharting.classic.util.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.solder.literal.DefaultLiteral;
import org.jboss.solder.reflection.HierarchyDiscovery;

public abstract class AbstractBean<T> implements Bean<T> {

    private final Set<Annotation> qualifiers;
    private final Set<Type> types;
    private final Class<T> beanClass;
    private final Class<? extends Annotation> scope;
    private final String name;
    private final boolean alternative;
    private final boolean nullable;

    public AbstractBean(Class<T> beanClass, Set<Type> types, Class<? extends Annotation> scope, String name,
            boolean alternative, boolean nullable, Set<Annotation> qualifiers) {
        this.beanClass = beanClass;
        this.types = types;
        this.scope = scope;
        this.name = name;
        this.alternative = alternative;
        this.nullable = nullable;
        if (qualifiers.isEmpty()) {
            this.qualifiers = Collections.<Annotation>singleton(DefaultLiteral.INSTANCE);
        } else {
            this.qualifiers = qualifiers;
        }
        
    }
    public AbstractBean(Class<T> beanClass, Class<?> types, Class<? extends Annotation> scope, String name,
            boolean alternative, boolean nullable, Set<Annotation> qualifiers) {
        this(beanClass, new HierarchyDiscovery(types).getTypeClosure(), scope, name, alternative, nullable, qualifiers);
    }

    public AbstractBean(Class<T> beanClass, Class<?> types, Class<? extends Annotation> scope, String name,
            boolean alternative, boolean nullable, Annotation... qualifiers) {
        this(beanClass, types, scope, name, alternative, nullable, new HashSet<Annotation>(Arrays.asList(qualifiers)));
    }

    public AbstractBean(Class<T> beanClass, Class<? extends Annotation> scope, String name, boolean alternative,
            boolean nullable, Annotation... qualifiers) {
        this(beanClass, beanClass, scope, name, alternative, nullable, qualifiers);
    }

    public AbstractBean(Class<T> beanClass, Class<? extends Annotation> scope, Annotation... qualifiers) {
        this(beanClass, scope, null, false, false, qualifiers);
    }

    public AbstractBean(Class<T> beanClass, Class<? extends Annotation> scope, Set<Annotation> qualifiers) {
        this(beanClass, beanClass, scope, null, false, false, qualifiers);
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return qualifiers;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public Class<?> getBeanClass() {
        return beanClass;
    }

    @Override
    public boolean isAlternative() {
        return alternative;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }
}
