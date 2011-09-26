package org.jboss.seam.classic.test.init;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@SuppressWarnings("serial")
@Name("deltaFactory")
@Scope(ScopeType.SESSION)
public class DeltaFactory implements Serializable {

    @Factory(value = "d1", scope = ScopeType.APPLICATION)
    public Delta createD1()
    {
        return new Delta("d1");
    }
    
    @Factory("d2")
    public Delta createD2()
    {
        return new Delta("d2");
    }
    
    @Factory(value = "d3", scope = ScopeType.EVENT)
    public Delta createD3()
    {
        return new Delta("d3");
    }
}
