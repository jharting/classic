package org.jboss.seam.classic.runtime.outjection;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@ApplicationScoped
public class RewritableContextManager {

    @Inject
    private BeanManager manager;
    @Inject
    @Any
    private Instance<OutjectedReferenceHolder> instance;

    public Object get(String name, Class<? extends Annotation> scope) {
        if (manager.getContext(scope).isActive()) {
            OutjectedReferenceHolder holder = getOutjectedReferenceHolder(scope);
            return holder.get(name);
        }
        return null;
    }

    public void set(String name, Object value, Class<? extends Annotation> scope) {
        if (!manager.getContext(scope).isActive()) {
            throw new IllegalStateException("Context not active: " + scope);
        }
        OutjectedReferenceHolder holder = getOutjectedReferenceHolder(scope);
        holder.put(name, value);
    }

    public boolean isSet(String name, Class<? extends Annotation> scope) {
        if (manager.getContext(scope).isActive()) {
            OutjectedReferenceHolder holder = getOutjectedReferenceHolder(scope);
            return holder.contains(name);
        }
        return false;
    }

    private OutjectedReferenceHolder getOutjectedReferenceHolder(Class<? extends Annotation> scope) {
        Instance<OutjectedReferenceHolder> instance = this.instance.select(OutjectedReferenceHolder.class,
                OutjectedReferenceHolder.ScopeQualifier.ScopeQualifierLiteral.valueOf(scope));
        if (instance.isAmbiguous() || instance.isUnsatisfied()) {
            throw new IllegalArgumentException("Unable to lookup " + OutjectedReferenceHolder.class.getName() + " for scope "
                    + scope);
        }
        return instance.get();
    }
}
