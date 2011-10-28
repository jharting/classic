package org.jboss.seam.classic.scope;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 * Registers scopes and extensions provided by Classic.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ScopeExtension implements Extension {

    private final StatelessContext statelessContext = new StatelessContext();

    void registerScopes(@Observes BeforeBeanDiscovery event) {
        event.addScope(StatelessScoped.class, true, false);
    }

    void registerContexts(@Observes AfterBeanDiscovery event, BeanManager manager) {
        event.addContext(statelessContext);
    }
}
