package cz.muni.fi.xharting.classic.test.bootstrap.install;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

@Name("delta")
@Install(genericDependencies = Bean1.class)
class Bean7 {

}
