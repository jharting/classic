package cz.muni.fi.xharting.classic.test.bootstrap;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.jboss.seam.annotations.Scope;

@SuppressWarnings("serial")
@Name("bravo")
@Scope(ScopeType.APPLICATION)
@Roles({ @Role(name = "b1", scope = ScopeType.SESSION), @Role(name = "b2", scope = ScopeType.EVENT) })
public class Bravo implements Serializable {

    private boolean initCalled = false;

    @Create
    public void init() {
        initCalled = true;
    }

    public boolean isInitCalled() {
        return initCalled;
    }

    public void ping() {
    }
}
