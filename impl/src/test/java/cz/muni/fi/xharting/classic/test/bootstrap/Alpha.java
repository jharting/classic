package cz.muni.fi.xharting.classic.test.bootstrap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("alpha")
@Scope(ScopeType.EVENT)
public class Alpha {

    private boolean initCalled = false;

    @Create
    public void init() {
        initCalled = true;
    }

    public boolean isInitCalled() {
        return initCalled;
    }
    
    public void ping()
    {
    }
}
