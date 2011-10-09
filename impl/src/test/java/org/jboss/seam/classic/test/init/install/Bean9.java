package org.jboss.seam.classic.test.init.install;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;

@Name("bean9")
@Install(true)
class Bean9 {

    @Factory("factory9")
    public String factory()
    {
        return "pong";
    }
}
