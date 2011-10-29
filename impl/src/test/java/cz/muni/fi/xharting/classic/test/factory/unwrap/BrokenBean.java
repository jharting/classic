package cz.muni.fi.xharting.classic.test.factory.unwrap;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

@Name("broken")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class BrokenBean {

    @Unwrap
    public String foo() {
        return "foo";
    }

    @Unwrap
    public String bar() {
        return "bar";
    }
}
