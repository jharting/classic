package org.jboss.seam.classic.scope;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;

public class StatelessContext implements Context {

    private ConcurrentMap<Contextual<?>, LastInstance<?>> lastInstances = new ConcurrentHashMap<Contextual<?>, LastInstance<?>>();

    @Override
    public Class<? extends Annotation> getScope() {
        return StatelessScoped.class;
    }

    @Override
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {

        T newInstance = contextual.create(creationalContext);

        @SuppressWarnings("unchecked")
        LastInstance<T> lastInstance = (LastInstance<T>) lastInstances.put(contextual, new LastInstance<T>(newInstance, creationalContext));
        if (lastInstance != null) {
            // we won't need this anymore
            contextual.destroy(lastInstance.getInstance(), lastInstance.getCreationalContext());
        }

        return newInstance;
    }

    @Override
    public <T> T get(Contextual<T> contextual) {
        return null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    private static class LastInstance<T> {
        private final T instance;
        private final CreationalContext<T> creationalContext;

        public LastInstance(T instance, CreationalContext<T> creationalContext) {
            this.instance = instance;
            this.creationalContext = creationalContext;
        }

        public T getInstance() {
            return instance;
        }

        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }
    }

    public void destroyInstances() {
        for (Contextual<?> contextual : lastInstances.keySet()) {
            destroyInstance(contextual);
        }
    }
    
    private <T> void destroyInstance(Contextual<T> contextual)
    {
        @SuppressWarnings("unchecked")
        LastInstance<T> lastInstance = (LastInstance<T>) lastInstances.get(contextual);
        contextual.destroy(lastInstance.getInstance(), lastInstance.getCreationalContext());
    }

}
