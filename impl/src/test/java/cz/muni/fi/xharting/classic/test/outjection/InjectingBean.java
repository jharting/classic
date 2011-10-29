package cz.muni.fi.xharting.classic.test.outjection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("injectingBean")
@Scope(ScopeType.APPLICATION)
public class InjectingBean {

    @In(required = false)
    private Message alpha;
    @In(required = false)
    private Message bravo;
    @In(required = false)
    private Message charlie;
    @In(required = false)
    private Message delta;
    @In(required = false)
    private Message echo;
    @In(required = false)
    private Message foxtrot;
    @In(required = false)
    private Message golf;
    @In(required = false)
    private String hotel;

    public Message getAlpha() {
        return alpha;
    }

    public Message getBravo() {
        return bravo;
    }

    public Message getCharlie() {
        return charlie;
    }

    public Message getDelta() {
        return delta;
    }

    public Message getEcho() {
        return echo;
    }

    public Message getFoxtrot() {
        return foxtrot;
    }

    public Message getGolf() {
        return golf;
    }

    public String getHotel() {
        return hotel;
    }
}
