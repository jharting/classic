package cz.muni.fi.xharting.classic.test.config;

import org.jboss.seam.annotations.Name;

@Name("foo")
public class Foo {

    private String foo = "bravo";
    private boolean bool = true;

    public String getFoo() {
        return foo;
    }

    public boolean isBool() {
        return bool;
    }
    
    public String delta()
    {
        return "delta";
    }

}
