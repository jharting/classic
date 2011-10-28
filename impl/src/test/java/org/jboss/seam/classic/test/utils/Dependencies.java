package org.jboss.seam.classic.test.utils;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public interface Dependencies {
    public static final Archive<?>[] SEAM_SOLDER = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadMetadataFromPom("pom.xml").artifact("org.jboss.solder:solder-impl").resolveAs(GenericArchive.class)
            .toArray(new Archive<?>[0]);

    public static final Archive<?>[] GUAVA = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml")
            .artifact("com.google.guava:guava").exclusion("*").resolveAs(GenericArchive.class).toArray(new Archive<?>[0]);

    public static final Archive<?>[] DOM4J = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml")
            .artifact("dom4j:dom4j").exclusion("*").resolveAs(GenericArchive.class).toArray(new Archive<?>[0]);
    
    public static final Archive<?>[] REFLECTIONS = DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("pom.xml")
            .artifact("org.reflections:reflections").resolveAs(GenericArchive.class).toArray(new Archive<?>[0]);
}
