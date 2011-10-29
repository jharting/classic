package cz.muni.fi.xharting.classic.test.bootstrap.install;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

@Name("alpha")
@Install(precedence = Install.APPLICATION)
class Bean1 {

}
