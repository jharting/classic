package org.jboss.seam.classic.init.metadata;

public abstract class AbstractObserverMethodDescriptor {

    private String type;

    public AbstractObserverMethodDescriptor(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
