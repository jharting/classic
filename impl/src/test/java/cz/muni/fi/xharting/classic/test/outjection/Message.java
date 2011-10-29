package cz.muni.fi.xharting.classic.test.outjection;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable {

    private String value;

    public Message() {
    }

    public Message(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
