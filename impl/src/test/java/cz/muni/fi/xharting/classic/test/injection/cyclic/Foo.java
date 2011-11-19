package cz.muni.fi.xharting.classic.test.injection.cyclic;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("foo")
@AutoCreate
public class Foo {

    @In
    private Bar bar;
    
    // invoked from the test
    public void a() {
        bar.b(); // cause reentrant call
        bar.d(); // verify that Foo is in consistent state
    }
    
    // reentrant method call
    public void c() {
        System.out.println();
    }
}
