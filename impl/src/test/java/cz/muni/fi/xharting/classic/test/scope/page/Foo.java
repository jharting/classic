package cz.muni.fi.xharting.classic.test.scope.page;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@SuppressWarnings("serial")
@Name("foo")
@Scope(ScopeType.PAGE)
public class Foo implements Serializable {

}
