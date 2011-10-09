package org.jboss.seam.classic.test.init.install;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

@Name("delta")
@Install(genericDependencies = Bean2.class)
class Bean6 {

}
