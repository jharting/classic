package cz.muni.fi.xharting.classic.test.bootstrap.scan;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamJar;
import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Namespace;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import cz.muni.fi.xharting.classic.bijection.BijectionInterceptor;
import cz.muni.fi.xharting.classic.bootstrap.scan.AbstractScanner;
import cz.muni.fi.xharting.classic.bootstrap.scan.ReflectionsScanner;
import cz.muni.fi.xharting.classic.bootstrap.scan.Scanner;
import cz.muni.fi.xharting.classic.event.RaiseEventInterceptor;
import cz.muni.fi.xharting.classic.test.bootstrap.scan.subpackage.AlphaJet;

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
