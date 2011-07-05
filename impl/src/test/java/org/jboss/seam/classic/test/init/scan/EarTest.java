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
import org.jboss.seam.annotations.Name;
import org.jboss.seam.classic.init.scan.ScannotationScanner;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

public class EarTest extends WarTest {

    @Deployment
    public static Archive<?> getDeployment() {

        EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "test.ear");

        ear.addAsLibrary(createSeamJar("alpha.jar", Alpha.class, EarTest.class, WarTest.class)); // EAR lib
        ear.addAsModule(createSeamJar("bravo.jar", Bravo.class)); // EAR module
        ear.addAsLibraries(SEAM_SOLDER).addAsLibraries(SCANNOTATION).addAsLibraries(SCANNOTATION_VFS);
        ear.addAsLibrary(createSeamClassic());
        ear.setApplicationXML("org/jboss/seam/classic/test/init/scan/application.xml");

        WebArchive war = createSeamWebApp("test.war", false, Alpha.class);
        ear.addAsModule(war);
        return ear;
    }

    @Test
    public void testScanning() {
        ScannotationScanner scanner = new ScannotationScanner(this.getClass().getClassLoader());
        scanner.scan();
        Set<Class<?>> classes = scanner.getClasses(Name.class.getName());
        assertEquals(2, classes.size());
        assertTrue(classes.contains(Alpha.class));
        assertTrue(classes.contains(Bravo.class));
    }
}
