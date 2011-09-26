package org.jboss.seam.classic.test.runtime.outjection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("injectingBean")
@Scope(ScopeType.APPLICATION)
public class InjectingBean {

    @In(required = false)
    private CharSequence alpha;
    @In(required = false)
    private CharSequence bravo;
    @In(required = false)
    private CharSequence charlie;
    @In(required = false)
    private CharSequence delta;
    @In(required = false)
    private CharSequence echo;
    @In(required = false)
    private StringWrapper foxtrot;
    @In(required = false)
    private StringWrapper golf;

    public CharSequence getAlpha() {
        return alpha;
    }

    public CharSequence getBravo() {
        return bravo;
    }

    public CharSequence getCharlie() {
        return charlie;
    }

    public CharSequence getDelta() {
        return delta;
    }

    public CharSequence getEcho() {
        return echo;
    }

    public StringWrapper getFoxtrot() {
        return foxtrot;
    }

    public StringWrapper getGolf() {
        return golf;
    }
}
