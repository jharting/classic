package org.jboss.seam.classic.test.config;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("injected")
@Scope(ScopeType.APPLICATION)
public class InjectedBean {

    @In
    private String alpha;
    @In
    private String bravo;
    @In
    private String charlie;
    @In
    private String delta;

    public String getAlpha() {
        return alpha;
    }

    public String getBravo() {
        return bravo;
    }

    public String getCharlie() {
        return charlie;
    }

    public String getDelta() {
        return delta;
    }
}
