package cz.muni.fi.xharting.classic.test.el;

import static cz.muni.fi.xharting.classic.test.util.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.core.Interpolator;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InterpolatorTest {

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", Animal.class, AnimalFactory.class);
    }

    @Test
    public void testStringInterpolation() {
        Interpolator interpolator = Interpolator.instance();
        assertEquals("The quick brown fox jumps over the lazy dog",
                interpolator.interpolate("The quick brown #{quick.kind} jumps over the lazy #{lazy.kind}"));
    }
}
