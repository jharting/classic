package cz.muni.fi.xharting.classic.test.startup;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class StartupTest {

    @Inject
    private StartupEventListener listener;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", Alpha.class, Bravo.class, Charlie.class, Delta.class, Echo.class,
                StartupEventListener.class, Superclass.class);
    }

    @Test
    public void testStartupWithDependencies() {
        List<String> startedComponents = listener.getStartedComponents();
        for (String component : new String[] { "alpha", "bravo", "delta", "echo" }) {
            assertTrue(startedComponents.contains(component));
            startedComponents.remove(component);
        }
        // charlie/foxtrot observed twice due to role
        assertEquals(2, startedComponents.size());
        assertEquals("charlie/foxtrot", startedComponents.get(0));
        assertEquals("charlie/foxtrot", startedComponents.get(1));
    }
}
