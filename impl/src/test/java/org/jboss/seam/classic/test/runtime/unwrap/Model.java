package org.jboss.seam.classic.test.runtime.unwrap;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("model")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class Model {

    private int value = 0;
    private List<Object> objects = new ArrayList<Object>();

    public int getValue() {
        return value;
    }

    public List<Object> getObjects() {
        return objects;
    }
    
    public void addObject()
    {
        objects.add(new Object());
    }
}
