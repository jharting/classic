package cz.muni.fi.xharting.classic.test.outjection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("outjectingBeanBrokenRequiredFieldNull")
@Scope(ScopeType.APPLICATION)
public class OutjectingBeanBrokenRequiredFieldNull {

    @SuppressWarnings("unused")
    @Out
    private String foo;
    
    public void ping()
    {
    }
    
}
