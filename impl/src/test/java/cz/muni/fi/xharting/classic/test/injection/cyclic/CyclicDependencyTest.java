package cz.muni.fi.xharting.classic.test.injection.cyclic;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Verifies that a cyclic dependency is allowed and reentrant method calls are handled properly.
 * 
 * @author Jozef Hartinger
 * 
 */
@RunWith(Arquillian.class)
public class CyclicDependencyTest {

    @Inject
    private Foo foo;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war").addPackage(CyclicDependencyTest.class.getPackage());
    }

    @Test
    public void testCyclicInjection() {
        foo.a();
    }
}
