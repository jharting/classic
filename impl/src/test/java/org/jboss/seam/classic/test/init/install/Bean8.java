package org.jboss.seam.classic.test.init.install;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

@Name("bean8")
@Install(false)
class Bean8 {

    @Factory("factory8")
    public String factory()
    {
        return "pong";
    }
}
