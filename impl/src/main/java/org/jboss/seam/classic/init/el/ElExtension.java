package org.jboss.seam.classic.init.el;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.solder.el.ELResolverProducer;

public class ElExtension implements Extension {

    /**
     * Simply get rid of Solder {@link ELResolverProducer}. Cross-archive specialization is broken so we cannot use that. 
     */
    public void vetoSolderElResolverProducer(@Observes ProcessAnnotatedType<ELResolverProducer> event) {
        if (event.getAnnotatedType().getJavaClass().equals(ELResolverProducer.class)) {
            event.veto();
        }
    }

}
