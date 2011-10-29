package cz.muni.fi.xharting.classic.test.factory.unwrap;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

@Name("objects")
@Scope(ScopeType.APPLICATION)
public class Objects {
    
    @In("model")
    private Model model;
    
    @Unwrap
    public List<Object> getObjects()
    {
        // create a copy, so that we can verify that getObjects() is called on every access to "objects"
        return new ArrayList<Object>(model.getObjects());
    }
}
