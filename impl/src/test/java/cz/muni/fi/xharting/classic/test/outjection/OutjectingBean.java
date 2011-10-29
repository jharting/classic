package cz.muni.fi.xharting.classic.test.outjection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("outjectingBean")
@Scope(ScopeType.APPLICATION)
@SuppressWarnings("unused")
public class OutjectingBean {

    @Out(scope = ScopeType.APPLICATION)
    private Message alpha = new Message("alpha");
    @Out
    private Message bravo = new Message("bravo");
    @Out("charlie")
    private Message foo = new Message("charlie");
    @Out(scope = ScopeType.SESSION)
    private Message delta = new Message("delta");
    @Out(scope = ScopeType.SESSION)
    private Message echo = new Message("echo");
    @Out(scope = ScopeType.APPLICATION)
    private Message foxtrot = new Message("foxtrot");
    @Out(scope = ScopeType.EVENT, required = false)
    private Message golf = new Message("golf");

    public void ping() {
    }

    public void setGolf(Message golf) {
        this.golf = golf;
    }
}
