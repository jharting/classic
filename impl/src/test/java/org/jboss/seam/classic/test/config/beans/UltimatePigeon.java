package org.jboss.seam.classic.test.config.beans;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Observer;

// not a Seam bean, added through XML
public class UltimatePigeon extends Pigeon {

    @Factory("foo")
    public Number getFoo() {
        return 0;
    }
    
    @Observer("bar")
    public void observeBar()
    {
    }
}
