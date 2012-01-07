package cz.muni.fi.xharting.classic.event;

import java.io.Serializable;

/**
 * Represents the payload of Seam 2 event. {@link #parameters} will passed as method parameters to the receiving method.
 * 
 * @author Jozef Hartinger
 * 
 */
public class EventPayload implements Serializable {

    private static final long serialVersionUID = 1320607414964449426L;
    private String name;
    private final Object[] parameters;

    public EventPayload(String name) {
        this(name, new Object[0]);
    }

    public EventPayload(String name,Object[] parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }
}
