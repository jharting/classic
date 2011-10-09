package org.jboss.seam.classic.test.init.install;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

@Name("bravo")
@Install(classDependencies = "non.existing.Class")
class Bean3 {

}
