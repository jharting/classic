package org.jboss.seam.classic.test.startup;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Name("echo")
@Scope(ScopeType.APPLICATION)
@Startup
public class Echo extends  Superclass {

    @Inject
    void initialize(@Started Event<String> event) {
        event.fire("echo");
    }
}
