package org.jboss.seam.classic.test.init.scan;

import static org.jboss.seam.classic.test.utils.Archives.createSeamJar;
import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Namespace;
import org.jboss.seam.classic.init.event.RaiseEventInterceptor;
import org.jboss.seam.classic.init.scan.AbstractScanner;
import org.jboss.seam.classic.init.scan.ReflectionsScanner;
import org.jboss.seam.classic.init.scan.Scanner;
import org.jboss.seam.classic.runtime.BijectionInterceptor;
import org.jboss.seam.classic.test.init.scan.subpackage.AlphaJet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class WarTest {

    private Scanner scanner;

    public static JavaArchive createSeamClassic() {
        return ShrinkWrap.create(JavaArchive.class, "seam-classic.jar")
                .addClasses(Scanner.class, AbstractScanner.class, ReflectionsScanner.class, Namespace.class)
                .addClasses(Name.class, BijectionInterceptor.class, RaiseEventInterceptor.class);
    }

    @Deployment
    public static Archive<?> getDeployment() {

        WebArchive war = createSeamWebApp("test.war", Alpha.class).addPackage(AlphaJet.class.getPackage());
        war.addAsLibrary(createSeamJar("bravo.jar", Bravo.class, Foo.class, Bar.class));
        war.addAsLibrary(createSeamJar("charlie.jar", Charlie.class));
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return war;
    }

    @Before
    public void scan() {
        if (scanner == null) {
            scanner = new ReflectionsScanner(WarTest.class.getClassLoader());
        }
    }

    @Test
    public void testAnnotationScanning() {
        Set<Class<?>> classes = scanner.getTypesAnnotatedWith(Name.class);
        assertEquals(3, classes.size());
        assertTrue(classes.contains(Alpha.class));
        assertTrue(classes.contains(Bravo.class));
        assertTrue(classes.contains(Charlie.class));
    }

    @Test
    public void testMetaAnnotationScanning() {
        Set<Class<?>> classes = ((ReflectionsScanner) getScanner()).getNonAnnotationTypesAnnotatedWithMetaAnnotation(Bar.class);
        assertEquals(1, classes.size());
        assertTrue(classes.contains(Bravo.class));
    }

    @Test
    public void testPackageScanning() {
        Set<Class<?>> namespaces = scanner.getTypesAnnotatedWith(Namespace.class);
        assertEquals(1, namespaces.size());
        Class<?> namespace = namespaces.iterator().next();
        assertTrue(namespace.isAnnotationPresent(Namespace.class));
        assertEquals("http://example.com/test", namespace.getAnnotation(Namespace.class).value());
    }

    public Scanner getScanner() {
        return scanner;
    }
}
