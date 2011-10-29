package cz.muni.fi.xharting.classic.test.startup;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

@Name("cyclic2")
@Scope(ScopeType.SESSION)
@Startup(depends = "cyclic1")
public class Cyclic2 {

}
