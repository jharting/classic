package org.jboss.seam.classic.learn;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Scope;

@Name("foo")
@Scope(ScopeType.EVENT)
//@Role(name = "foo2", scope = ScopeType.APPLICATION)
public class Foo {

    @Out
    private String baz = "bazz";
    
    @Factory("bar")
    public String bar()
    {
        return null;
//        return "bar";
    }
    
    @Factory("baz")
    public void baz()
    {
        
    }
}
