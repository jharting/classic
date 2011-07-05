package org.jboss.seam.classic.test.init.scan;

import static org.jboss.seam.classic.test.utils.Archives.createSeamJar;
import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.jboss.seam.classic.test.utils.Dependencies.SCANNOTATION;
import static org.jboss.seam.classic.test.utils.Dependencies.SCANNOTATION_VFS;
import static org.jboss.seam.classic.test.utils.Dependencies.SEAM_SOLDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.classic.init.scan.ScannotationScanner;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WarTest {

    public static JavaArchive createSeamClassic() {
        return ShrinkWrap.create(JavaArchive.class, "seam-classic.jar").addClasses(ScannotationScanner.class)
                .addClass(Name.class);
    }
    
    @Deployment
    public static Archive<?> getDeployment() {

        WebArchive war = createSeamWebApp("test.war", false, Alpha.class).addAsLibraries(SEAM_SOLDER).addAsLibraries(SCANNOTATION)
                .addAsLibraries(SCANNOTATION_VFS);
        war.addAsLibrary(createSeamClassic());
        war.addAsLibrary(createSeamJar("bravo.jar", Bravo.class));
        war.addAsLibrary(createSeamJar("charlie.jar", Charlie.class));
        return war;
    }

    @Test
    public void testScanning() {
        ScannotationScanner scanner = new ScannotationScanner(this.getClass().getClassLoader());
        scanner.scan();
        Set<Class<?>> classes = scanner.getClasses(Name.class.getName());
        assertEquals(3, classes.size());
        assertTrue(classes.contains(Alpha.class));
        assertTrue(classes.contains(Bravo.class));
        assertTrue(classes.contains(Charlie.class));
    }

}
