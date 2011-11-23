package cz.muni.fi.xharting.classic.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.solder.literal.NamedLiteral;

public abstract class AbstractLegacyFactory<T> implements Bean<T> {

    private final String name;
    private final Class<? extends Annotation> scope;
    private final BeanManager manager;
    private final Set<Type> types = new HashSet<Type>();

    public AbstractLegacyFactory(String name, Class<? extends Annotation> scope, BeanManager manager) {
        this.name = name;
        this.scope = scope;
        this.manager = manager;
    }

    protected void addTypes(Type... types) {
        for (Type type : types) {
            this.types.add(type);
        }
    }
    
    protected void addTypes(Set<Type> types) {
        this.types.addAll(types);
    }

    @Override
    public Set<Type> getTypes() {
        return types;
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.<Annotation> singleton(new NamedLiteral(name));
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }
    
    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
        creationalContext.release();
    }

    protected BeanManager getManager() {
        return manager;
    }
}
