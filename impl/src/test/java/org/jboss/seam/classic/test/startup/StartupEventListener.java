package org.jboss.seam.classic.test.startup;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class StartupEventListener {

    private final List<String> startedComponents = new ArrayList<String>();

    void observeBeanStartup(@Observes @Started String string) {
        startedComponents.add(string);
    }

    public List<String> getStartedComponents() {
        return startedComponents;
    }
}
