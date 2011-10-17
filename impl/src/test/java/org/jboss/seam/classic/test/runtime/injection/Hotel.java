package org.jboss.seam.classic.test.runtime.injection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("hotel")
@Scope(ScopeType.APPLICATION)
public class Hotel {

    @In
    private Echo echo;
    
    public void checkInjection()
    {
        if (echo == null)
        {
            throw new AssertionError("echo not injected");
        }
    }
}
