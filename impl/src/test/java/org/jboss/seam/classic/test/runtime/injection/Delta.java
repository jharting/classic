package org.jboss.seam.classic.test.runtime.injection;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("delta")
@Scope(ScopeType.APPLICATION)
public class Delta extends AbstractComponent {

}
