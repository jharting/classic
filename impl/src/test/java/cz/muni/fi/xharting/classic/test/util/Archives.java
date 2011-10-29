package cz.muni.fi.xharting.classic.test.util;

import java.io.InputStream;

import javax.enterprise.inject.spi.Extension;

import org.jboss.osgi.testing.ManifestBuilder;
import org.jboss.seam.RequiredException;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.filter.ExcludeRegExpPaths;

import cz.muni.fi.xharting.classic.Seam2ManagedBean;
import cz.muni.fi.xharting.classic.bootstrap.CoreExtension;
import cz.muni.fi.xharting.classic.el.ElExtension;
import cz.muni.fi.xharting.classic.intercept.InterceptorExtension;
import cz.muni.fi.xharting.classic.scope.ScopeExtension;

public class Archives {

    public static JavaArchive createSeamClassic() {
        JavaArchive jar =  ShrinkWrap
                .create(JavaArchive.class, "seam-classic.jar")
                
                .addPackages(true, new ExcludeRegExpPaths(".*test.*"), RequiredException.class.getPackage(), Seam2ManagedBean.class.getPackage())
                .addAsServiceProvider(Extension.class, CoreExtension.class, InterceptorExtension.class,
                        ScopeExtension.class, ElExtension.class).addAsManifestResource("META-INF/beans.xml", "beans.xml");
        addDependencyToManifest(jar, "org.slf4j.impl");
        return jar;
    }

    public static JavaArchive createSeamJar(String name, Class<?>... classes) {
        return ShrinkWrap.create(JavaArchive.class, name).addClasses(classes)
                .addAsResource(EmptyAsset.INSTANCE, "seam.properties");
    }

    public static WebArchive createSeamWebApp(String name, Class<?>... classes) {
        return createSeamWebApp(name, true, true, classes);
    }

    public static WebArchive createSeamWebApp(String name, boolean bundleSeamClassic, boolean bundleBeansDotXml,
            Class<?>... classes) {
        WebArchive war = ShrinkWrap.create(WebArchive.class, name).addAsResource(EmptyAsset.INSTANCE, "seam.properties")
                .addClasses(classes);
        if (bundleBeansDotXml) {
            // beans.xml should not be required, but we bundle it since we want CDI to be enabled for tests
            war.addAsWebInfResource("META-INF/beans.xml", "beans.xml");
        }

        if (bundleSeamClassic) {
            war.addAsLibrary(createSeamClassic()).addAsLibraries(Dependencies.SEAM_SOLDER, Dependencies.DOM4J, Dependencies.REFLECTIONS);
        }
        return war;
    }
    
    public static <T extends ManifestContainer<?>> T addDependencyToManifest(T archive, final String dependencies)
    {
        archive.setManifest(new Asset() {

            @Override
            public InputStream openStream() {
                return ManifestBuilder.newInstance().addManifestHeader("Dependencies", dependencies)
                        .openStream();
            }
        });
        return archive;
    }
}
