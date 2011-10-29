package cz.muni.fi.xharting.classic.test.bootstrap.install;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

@Name("charlie")
@Install(precedence = Install.BUILT_IN)
class Bean4 {

}
