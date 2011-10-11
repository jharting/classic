package org.jboss.seam.classic.test.startup;

import javax.inject.Inject;

public class Superclass {

    @Inject
    private StartupEventListener listener;

    protected void verifyStartupOrder(String... dependencies) {
        for (String dependency : dependencies) {
            if (!listener.getStartedComponents().contains(dependency)) {
                throw new AssertionError("Dependency not started: " + dependency);
            }
        }
    }
}
