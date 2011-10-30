package cz.muni.fi.xharting.classic.test.el;

public class Animal {

    private final String kind;

    protected Animal() {
        kind = null;
    }

    public Animal(String kind) {
        this.kind = kind;
    }

    public String getKind() {
        return kind;
    }
}
