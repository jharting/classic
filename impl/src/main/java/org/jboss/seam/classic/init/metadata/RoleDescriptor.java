package org.jboss.seam.classic.init.metadata;

import org.jboss.seam.ScopeType;

public class RoleDescriptor {

    private String name;
    private ScopeType scope;

    public RoleDescriptor(String name, ScopeType scope) {
        this.name = name;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public ScopeType getScope() {
        return scope;
    }
}
