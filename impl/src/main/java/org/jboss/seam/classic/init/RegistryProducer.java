package org.jboss.seam.classic.init;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.jboss.seam.classic.init.metadata.MetadataRegistry;

@ApplicationScoped
public class RegistryProducer {

    @Produces
    @ApplicationScoped
    public MetadataRegistry getRegistry(CoreExtension extension) {
        return extension.getRegistry();
    }

}
