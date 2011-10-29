package cz.muni.fi.xharting.classic.test.outjection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("voidFactory")
@Scope(ScopeType.APPLICATION)
public class VoidFactoryBean {
    
    @SuppressWarnings("unused")
    @Out(scope = ScopeType.EVENT)
    private String hotel;

    @Factory(autoCreate = true, value = "hotel")
    public void setEcho() {
        this.hotel = "hotel";
    }
}
