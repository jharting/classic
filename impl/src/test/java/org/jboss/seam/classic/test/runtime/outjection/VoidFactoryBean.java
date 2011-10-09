package org.jboss.seam.classic.test.runtime.outjection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("voidFactory")
@Scope(ScopeType.APPLICATION)
public class VoidFactoryBean {
    
    @SuppressWarnings("unused")
    @Out(scope = ScopeType.APPLICATION)
    private String hotel;

    @Factory(autoCreate = true, value = "hotel")
    public void setEcho() {
        this.hotel = "hotel";
    }
}
