package cz.muni.fi.xharting.classic.test.injection.cyclic;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("bar")
@AutoCreate
public class Bar {

    @In
    private Foo foo;

    public void b() {
        foo.c();
    }

    public String d() {
        return "d";
    }
}
