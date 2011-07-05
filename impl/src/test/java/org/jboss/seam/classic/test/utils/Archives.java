package org.jboss.seam.classic.test.utils;

import javax.enterprise.inject.spi.Extension;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.classic.init.SeamClassicExtension;
import org.jboss.seam.classic.util.literals.PreDestroyLiteral;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class Archives {

    public static JavaArchive createSeamClassic() {
        return ShrinkWrap.create(JavaArchive.class, "seam-classic.jar")
                .addPackages(true, SeamClassicExtension.class.getPackage()) // org.jboss.seam.classic.init
                .addPackages(true, PreDestroyLiteral.class.getPackage()) // org.jboss.seam.classic.util.literal
                .addPackages(false, ScopeType.class.getPackage()).addPackages(true, Name.class.getPackage())
                .addAsServiceProvider(Extension.class, SeamClassicExtension.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public static JavaArchive createSeamJar(String name, Class<?>... classes) {
        return ShrinkWrap.create(JavaArchive.class, name).addClasses(classes)
                .addAsResource(EmptyAsset.INSTANCE, "seam.properties");
    }

    public static WebArchive createSeamWebApp(String name, Class<?>... classes) {
        return createSeamWebApp(name, true, classes);
    }

    public static WebArchive createSeamWebApp(String name, boolean bundleSeamClassic, Class<?>... classes) {
        WebArchive war = ShrinkWrap.create(WebArchive.class, name).addAsResource(EmptyAsset.INSTANCE, "seam.properties")
                .addClasses(classes);
        // beans.xml should not be required, but we bundle it since we want CDI to be enabled for tests
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        
        if (bundleSeamClassic) {
            war.addAsLibrary(createSeamClassic()).addAsLibraries(Dependencies.SEAM_SOLDER)
                    .addAsLibraries(Dependencies.SCANNOTATION).addAsLibraries(Dependencies.SCANNOTATION_VFS);
        }
        return war;
    }

}
