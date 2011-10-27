package org.jboss.seam.classic.init.metadata;

import java.lang.annotation.Annotation;

import javax.enterprise.context.RequestScoped;

import org.jboss.seam.ScopeType;
import org.jboss.seam.classic.util.Seam2Utils;

public abstract class AbstractFactoryDescriptor extends AbstractManagedInstanceDescriptor {

    private final String name;
    private final ScopeType scope;

    public AbstractFactoryDescriptor(String name, ScopeType scope, boolean autoCreate) {
        super(autoCreate);
        this.name = name;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public ScopeType getScope() {
        return scope;
    }
    
    /**
     * Translates Seam 2 ScopeType to matching CDI scope.
     */
    public Class<? extends Annotation> getCdiScope() {
        if (scope == null || scope == ScopeType.UNSPECIFIED)
        {
            return RequestScoped.class;
        }
        return Seam2Utils.transformExplicitLegacyScopeToCdiScope(scope);
    }
}
