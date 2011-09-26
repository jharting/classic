package org.jboss.seam.classic.test.runtime.outjection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("outjectingBean")
@Scope(ScopeType.APPLICATION)
@SuppressWarnings("unused")
public class OutjectingBean {

    @Out(scope = ScopeType.APPLICATION)
    private CharSequence alpha = "alpha";
    @Out
    private CharSequence bravo = "bravo";
    @Out("charlie")
    private CharSequence foo = "charlie";
    @Out(scope = ScopeType.SESSION)
    private CharSequence delta = "delta";
    @Out(scope = ScopeType.SESSION)
    private CharSequence echo = "echo";
    @Out(scope = ScopeType.APPLICATION)
    private CharSequence foxtrot = "foxtrot";
    @Out(scope = ScopeType.EVENT, required = false)
    private StringWrapper golf = new StringWrapper("golf");

    public void ping() {
    }

    public void setGolf(StringWrapper golf) {
        this.golf = golf;
    }
}
