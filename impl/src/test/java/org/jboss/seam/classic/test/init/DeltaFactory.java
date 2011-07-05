package org.jboss.seam.classic.test.init;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@SuppressWarnings("serial")
@Name("deltaFactory")
@Scope(ScopeType.SESSION)
public class DeltaFactory implements Serializable {

    @SuppressWarnings("unused")
    @Out(value = "d3", scope = ScopeType.EVENT)
    private Delta d3;
    
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
    
    @Factory("d3")
    public void createD3()
    {
        d3 = new Delta("d3");
    }
}
