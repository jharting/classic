package org.jboss.seam.classic.test.runtime.outjection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("factory")
@Scope(ScopeType.APPLICATION)
public class FactoryBean {

    @Factory(scope = ScopeType.EVENT, value = "delta")
    public CharSequence getDelta() {
        return "factoryDelta";
    }

    @Factory(autoCreate = true, scope = ScopeType.SESSION, value = "echo")
    public CharSequence getEcho() {
        return "factoryEcho";
    }

    @Factory(autoCreate = true, scope = ScopeType.SESSION, value = "foxtrot")
    public StringWrapper getFoxtrot() {
        return new StringWrapper("factoryFoxtrot");
    }

    @Factory(autoCreate = true, scope = ScopeType.SESSION, value = "golf")
    public StringWrapper getGolf() {
        return new StringWrapper("factoryGolf");
    }

}
