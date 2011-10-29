package cz.muni.fi.xharting.classic.bootstrap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import cz.muni.fi.xharting.classic.metadata.MetadataRegistry;

@ApplicationScoped
public class RegistryProducer {

    @Produces
    @ApplicationScoped
    public MetadataRegistry getRegistry(CoreExtension extension) {
        return extension.getRegistry();
    }

}
