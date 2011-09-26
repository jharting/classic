package org.jboss.seam.classic.test.runtime.outjection;

import java.io.Serializable;

@SuppressWarnings("serial")
public class StringWrapper implements Serializable {

    private String value;

    public StringWrapper() {
    }

    public StringWrapper(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
