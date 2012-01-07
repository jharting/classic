package cz.muni.fi.xharting.classic.scope.stateless;

import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

/**
 * A new instance of bean is created on every invocation. This scope is useful for implementing unwrapping methods.
 * 
 * @author Jozef Hartinger
 * 
 */
public class StatelessContext implements Context {

    @Override
    public Class<? extends Annotation> getScope() {
        return StatelessScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        return contextual.create(creationalContext);
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
