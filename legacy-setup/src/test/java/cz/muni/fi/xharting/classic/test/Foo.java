package cz.muni.fi.xharting.classic.test;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("foo")
@Scope(ScopeType.EVENT)
public class Foo {

    
    @Out(required = false)
    private String bazzz;
    
    public void ping() {
        
    }
}
