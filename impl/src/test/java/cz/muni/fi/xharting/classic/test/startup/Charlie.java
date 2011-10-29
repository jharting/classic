package cz.muni.fi.xharting.classic.test.startup;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Name("charlie")
@Scope(ScopeType.APPLICATION)
@Role(name = "foxtrot", scope = ScopeType.APPLICATION)
@Startup(depends = { "delta", "echo" })
public class Charlie extends Superclass {

    @Inject
    void initialize(@Started Event<String> event) {
        verifyStartupOrder("delta", "echo");
        event.fire("charlie/foxtrot");
    }
}
