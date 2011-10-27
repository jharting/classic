package org.jboss.seam.classic.test.config;

import static org.jboss.seam.classic.test.utils.Archives.createSeamWebApp;
import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.core.Events;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EventAndFactoryTest {

    @Inject
    private InjectedBean bean;

    @Deployment
    public static WebArchive getDeployment() {
        return createSeamWebApp("test.war", InjectedBean.class, Foo.class, ObservingBean.class).addAsResource(
                "org/jboss/seam/classic/test/config/event-factory-components.xml", "META-INF/components.xml");
    }

    @Test
    public void testValueExpressionFactory1() {
        assertEquals("alpha", bean.getAlpha());
    }

    @Test
    public void testValueExpressionFactory2() {
        assertEquals("bravo", bean.getBravo());
    }

    @Test
    public void testValueExpressionFactory3() {
        assertEquals("true", bean.getCharlie());
    }

    @Test
    public void testMethodExpressionFactory() {
        assertEquals("delta", bean.getDelta());
    }

    @Test
    public void testEvents(Events events, ObservingBean observingBean) {
        events.raiseEvent("event");
        assertEquals(1, observingBean.getAlpha());
        assertEquals(1, observingBean.getBravo());
    }

}
