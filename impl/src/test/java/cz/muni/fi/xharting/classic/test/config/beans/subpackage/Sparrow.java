package cz.muni.fi.xharting.classic.test.config.beans.subpackage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("sparrow")
@Scope(ScopeType.EVENT)
public class Sparrow {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}
