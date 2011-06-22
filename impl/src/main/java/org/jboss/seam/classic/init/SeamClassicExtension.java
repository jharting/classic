package org.jboss.seam.classic.init;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class SeamClassicExtension implements Extension {
    
    private ClassicBeanTransformer classicBeanTransformer = new ClassicBeanTransformer();

    public void init(@Observes BeforeBeanDiscovery event)
    {
        // TODO scan
    }
    
    public void init2(@Observes AfterBeanDiscovery event)
    {
//        event.
    }
    
}
